/**
 * Copyright (c) 2016 Peter A McGill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
**/
package org.pmg.jms.sudoku.genmonitor;

import org.pmg.jms.sudoku.genprofile.Profile;
import java.util.HashSet;
import org.pmg.jms.sudoku.genresolvar.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class Rankoten {

    private static final Logger LOG = LoggerFactory.getLogger(Rankoten.class);        
    private final String className = getClass().getSimpleName();
    private final RemnantScore remnantScore = new RemnantScore();
    private final HashSet<TrialOption> topSeeds = new HashSet<>();    
    private final String peerId;
    public String rankToken = "";
    public int trialRank = 9;
       
    public Rankoten(String peerId) {
    
        this.peerId = peerId;
    }
    
    public TrialOption getTopSeed(Profile profile) {

        if (topSeeds.isEmpty())
            return new TrialOption();
        
        TrialOption topSeed = topSeeds.iterator().next();
        topSeed.peerId = peerId;
        topSeed.rank = trialRank;
        topSeed.allPeers = profile.getAllPeers(topSeed.key);
        return topSeed;
    }

    public boolean hasTopSeeds() {
        
        return !topSeeds.isEmpty();
    }
    
    public void evalRank(Snapshot snapshot) {
        
        rankToken = remnantScore.getRankToken(snapshot);        
        switch (rankToken) {
            case "1:1:0" : 
                trialRank = 0; 
                break;
            case "2:2:0" : 
                trialRank = 1; 
                break;
            case "2:1:1" : 
                trialRank = 2; 
                break;
            case "3:2:1" : 
                trialRank = 3; 
                break;
            case "3:1:2" : 
                trialRank = 4; 
                break;
            case "4:2:2" : 
                trialRank = 5; 
                break;
            case "4:1:3" : 
                trialRank = 6; 
                break;
            case "5:2:3" : 
                trialRank = 7; 
                break;
            case "5:3:2" : 
                trialRank = 8; 
                break;
            default :
                trialRank = 9; 
                break;
        }
        LOG.info("[{}[{}] unit ranking : {},{},{}",className,peerId,trialRank,
                                        rankToken,snapshot.remnantKeys.size());

    }
    
    @Override
    public String toString() {

        if (topSeeds.isEmpty())
            return "[Rankoten] is empty";
        String objText = "[Rankoten] top seeds ";
        while (topSeeds.iterator().hasNext())
            objText += "," + topSeeds.iterator().next().toString();
        return objText;
    }

    private class RemnantScore {
        private int totScore2 = 0;
        private int totScore3 = 0;
        public int topScore = 3;

        public String getRankToken(Snapshot snapshot) {
        
            reset();
            String options;
            for( String key: snapshot.remnantKeys ) {
                options = snapshot.unit.get(key);
                evalTopScore(key,options);
            }
            int count = totScore2 + totScore3;
            return String.format("%d:%d:%d",count,totScore2,totScore3);

        }

        private void evalTopScore(String key,String options) {
        
            int seedScore = options.length();
            if (seedScore == 2)
                totScore2++;
            else if (seedScore == 3)
                totScore3++;
            else
                return;
            if (seedScore > topScore)
                return;
            if (seedScore < topScore) {
                topScore = seedScore;
                topSeeds.clear();            
            }
            topSeeds.add(new TrialOption(key,options));
        }
        
        private void reset() {

            topSeeds.clear();
            totScore2 = 0;
            totScore3 = 0;
            topScore = 3;
        }
    }
}
