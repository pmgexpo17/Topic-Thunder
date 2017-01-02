/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the 'License'); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//var amq = org.activemq.Amq;

org.activemq.Chat = function() {
	var amq = org.activemq.Amq;
	
	var adapter = org.activemq.AmqAdapter;

	var serverStatus = 'offline';
	
	var sessionId = null;
	
	var responseCache = [];

	var gameId = '';
	
	var putCount = 0;

	var statusHandler = function(message) {

		//var serial = new XMLSerializer();
		//$("#status").val(serial.serializeToString(message));
		//alert('XML : ' + serial.serializeToString(message));
		
		var action = message.getAttribute('action');
		var from = message.getAttribute('from');
		//var method = message.getAttribute('method');

		switch (action) {
			// Incoming chat message
			case 'join' :
				putGameId(message);
				break;
			case 'resolve' :
				putCount = 0;
				putSolution(message);				
				break;
			case 'error1' :
				putCount = 0;
				putSolution(message);			
				setTimeout(notifyError1,500);
				break;
			case 'error2' :
			    // this is for invalid startMap errors 
				putCount = 0;
				notifyError2(message);
				break;	
			case 'test' :
				alert('YEH YEH 100');
				break;
			case 'timeout' :
				putCount = 0;
				putSolution(message);			
				setTimeout(notifyTimeout,500);
				break;
			case 'ping' :
				serverStatus = 'active';
				break;
			case 'leave':
				alert("User has left : " + from);
				break;
			default :
				break;
		}
	};
	
	var toggleGame = function(state) {
		
		$('#gameB1').prop('disabled', state);
		$('#gameB2').prop('disabled', state);
	};
	
	var notifyError1 = function() {
		
		alert('Error : your start map has at least one wrong cell');
	};

	var notifyError2 = function(message) {

		var element = message.childNodes[putCount];
		if (element === undefined) {
			putCount++;
			setTimeout(notifyError2,10,message);
			return;
		}
		if (element.nodeType != 1) {
			putCount++;
			setTimeout(notifyError2,10,message);
			return;
		}	
		var nodename = element.nodeName;
		var errText = element.textContent;
		alert(errText);
		toggleGame(false);
	};

	var notifyTimeout = function() {
		
		alert('Alert : your session has timed out');
	};

	var putGameId = function(message) {
	
	    var element = message.getElementsByTagName("gameId");
	    gameId = element[0].textContent;
	    $("#status").val('Game id : ' + gameId);
	}

	var putSolution = function(message) {

		if (putCount == message.childNodes.length) {
			putResultPic();
			return;
		}
		var element = message.childNodes[putCount];
		if (element === undefined) {
			putCount++;
			setTimeout(putSolution,10,message);
			return;
		}
		if (element.nodeType != 1) {
			putCount++;
			setTimeout(putSolution,10,message);
			return;
		}	
		var nodename = element.nodeName;
		var data = element.textContent;
		var selector = "#playbox :input[name='%s']".replace('%s',nodename);
		if ($(selector).hasClass("whitecell")) {
			$(selector).removeClass("whitecell");
			$(selector).addClass("whitehit");
		}
		else if ($(selector).hasClass("greycell")) {
			$(selector).removeClass("greycell");
			$(selector).addClass("greyhit");
		}
		$(selector).val(data);
		putCount++;
		setTimeout(putSolution,10,message);
	};
		
	var getKeyCode = function (ev) {
		var keyc;
		if (window.event) keyc = window.event.keyCode;
		else keyc = ev.keyCode;
		return keyc;
	};

	var addEvent = function(obj, type, fn) {
		if (obj.addEventListener)
			obj.addEventListener(type, fn, false);
		else if (obj.attachEvent) {
			obj["e"+type+fn] = fn;
			obj[type+fn] = function() { obj["e"+type+fn]( window.event ); }
			obj.attachEvent( "on"+type, obj[type+fn] );
		}
	};

	var getSquarePeer = function(cell) {
		var row1 = cell.substr(0,1);
		var col1 = cell.substr(1,1);

		var row2, col2;
		switch (row1) {
			case 'A':
			case 'B':
			case 'C':
				row2 = 'A';
				break;
			case 'D':
			case 'E':
			case 'F':
				row2 = 'B';
				break;
			case 'G':
			case 'H':
			case 'I':
				row2 = 'C';
				break;
			default : row2 = '';
		}
		switch (col1) {
			case '1':
			case '2':
			case '3':
				col2 = '1';
				break;
			case '4':
			case '5':
			case '6':
				col2 = '2';
				break;
			case '7':
			case '8':
			case '9':
				col2 = '3';
				break;
			default : col2 = '';
		}
		return 'SQU' + row2 + col2;
	};

	var getStartMap = function() {

		var gridMap = {'count':0};
		var cell, row, col, square, data;
		var count = 0;
		$("#playbox :input[type='text']").each(function(i, elem) {
			if ($(this).val() == '')
				return true;
			cell = $(this).attr("name");
			row = 'ROW' + cell.substr(0,1);
			col = 'COL' + cell.substr(1,1);
			square = getSquarePeer(cell);
			data = (gridMap[row] === undefined) ? '' : gridMap[row] + ',';
			gridMap[row] = data + cell + ',' + $(this).val();
			data = (gridMap[col] === undefined) ? '' : gridMap[col] + ',';
			gridMap[col] = data + cell + ',' + $(this).val();
			data = (gridMap[square] === undefined) ? '' : gridMap[square] + ',';
			gridMap[square] = data + cell + ',' + $(this).val();
			count++;
		});
		gridMap['count'] = count;
		return gridMap;
	};

	var sendStartMap = function() {
		
		var startMap = getStartMap();
		var xmlNode;
		var startDoc = [];
		$.each(startMap,function(key,value) {
			xmlNode = '<%s>'.replace('%s',key) + value + '</%s>'.replace('%s',key);
			startDoc.push(xmlNode);
		});
		startDoc.unshift(xmlNode);
		var startText = startDoc.join('');
		org.activemq.Chat.join(startText);
	};

	var getResultMap = function() {

		var gridMap = {};
		var cell;
		$("#playbox :input[type='text']").each(function(i, elem) {
			if ($(this).val() == '')
				return true;
			cell = $(this).attr("name");
			gridMap[cell] = $(this).val();
		});
		return gridMap;
	};

	var putResultPic = function() {
		
		var resultMap = getResultMap();
		var resultPic = [];
		var rowCount = 0;
		var rows = ['A','B','C','D','E','F','G','H','I'];
		while (rows.length > 0) {
			rowCount++;
			var picText = '';
			var row = rows.shift();
			for( var i=1; i<=9; i++ ) {				
				var cell = row + i;
				picText += (resultMap[cell] === undefined) ? ' ' : resultMap[cell] + ' ';
				picText += ((i % 3) == 0) && (i < 9) ? '|' : '';
			}
			resultPic.push(picText);
			if ( ((rowCount % 3) == 0) && (rowCount < 9) )
				resultPic.push('------+------+------');				
		}
		$("#status").val(resultPic.join('\n'));
		toggleGame(false);
	};
		
	var getStartGrid = function() {

		var startTokens = $("#status").val().trim();
		if ((startTokens.length == 0) || (startTokens == 'paste start pattern here'))
			return JSON.parse('{}');
		if ((startTokens.indexOf('.') < 0) && (startTokens.indexOf(',') > 0))
			return JSON.parse(startTokens);
		var tokens = startTokens.split('');
		var startList = [];
		var rows = ['A','B','C','D','E','F','G','H','I'];
		var row, cell;
		while (rows.length > 0) {
			row = rows.shift();
			for( var i=1; i<=9; i++ ) {				
				if (tokens[0] != '.') {
					cell = '"%s":%d'.replace('%s',row + i);
					startList.push(cell.replace('%d',tokens[0]));
				}
				tokens.shift();
			}
		}
		startTokens = "{%s}".replace('%s',startList.join(','));
		return JSON.parse(startTokens);
	};
		
	var initEventHandlers = function() {
		
		addEvent(document.getElementById('gameB1'), 'click', function() {
			if (serverStatus == 'offline') {
				alert('Sudoku server is offline');
				return true;
			}
			sendStartMap();
			toggleGame(true);
			return true;
		});
		
		addEvent(document.getElementById('gameB2'), 'click', function() {
			var startGrid;
			try {

				startGrid = getStartGrid();
			} catch(e) {
				throw(e);
			}
			var cell, value;
			$("#playbox :input[type='text']").each(function(i, elem) {
				cell = $(this).attr("name");
				value = (startGrid[cell] === undefined) ? '' : startGrid[cell];
				$(this).val(value);
				if ($(this).hasClass("greyhit")) {
					$(this).removeClass("greyhit");
					$(this).addClass("greycell");
				}
				else if ($(this).hasClass("whitehit")) {
					$(this).removeClass("whitehit");
					$(this).addClass("whitecell");
				}
			});
		});
	};
			
	return {
		leave: function(destination) {
			var param = {state:'play',action:'',method:'leave'};
			amq.sendMessage(destination,null,param);
		},
		join: function(startDoc) {
			var destName = 'queue://WEB.CLIENT/sudoku/play';
			var param = {state:'play',action:'join',method:'send'};
			amq.addListener(destName,startDoc,param,statusHandler);	
		},
		messageHandler : statusHandler,
		query: function() {
			var param = {state:'play',action:'query',method:'send'};
			amq.sendMessage('',null,param);
		},
		
		init: function() {
			
			initEventHandlers();
		}
	}
}();












