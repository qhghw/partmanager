UploadPanel = Ext.extend(Ext.Panel, {
	fileList : null,
	swfupload : null,
	progressBar : null,
	progressInfo : null,
	uploadInfoPanel : null,
	constructor : function(config) {
		this.progressInfo = {
			filesTotal : 0,
			filesUploaded : 0,
			bytesTotal : 0,
			bytesUploaded : 0,
			currentCompleteBytes : 0,
			lastBytes : 0,
			lastElapsed : 1,
			lastUpdate : null,
			timeElapsed : 1
		};
		this.uploadInfoPanel = new Ext.Panel({
			region : 'north',
			height : 65,
			baseCls : '',
			collapseMode : 'mini',
			split : true,
			border : false
		});
		this.progressBar = new Ext.ProgressBar({
			text : '等待中 0 %',
			animate : true
		});
		var autoExpandColumnId = Ext.id('fileName');
		this.uploadInfoPanel.on('render', function() {
			this.getProgressTemplate().overwrite(this.uploadInfoPanel.body, {
				filesUploaded : 0,
				filesTotal : 0,
				bytesUploaded : '0 bytes',
				bytesTotal : '0 bytes',
				timeElapsed : '00:00:00',
				timeLeft : '00:00:00',
				speedLast : '0 bytes/s',
				speedAverage : '0 bytes/s'
			});
		}, this);
		this.fileList = new Ext.grid.GridPanel({
			border : false,
			enableColumnMove : false,
			enableHdMenu : false,
			columns : [new Ext.grid.RowNumberer(), {
				header : '文件名',
				width : 100,
				dataIndex : 'fileName',
				sortable : false,
				fixed : true,
				renderer : this.formatFileName,
				id : autoExpandColumnId
			}, {
				header : '大小',
				width : 80,
				dataIndex : 'fileSize',
				sortable : false,
				fixed : true,
				renderer : this.formatFileSize,
				align : 'right'
			}, {
				header : '类型',
				width : 60,
				dataIndex : 'fileType',
				sortable : false,
				fixed : true,
				renderer : this.formatIcon,
				align : 'center'
			}, {
				header : '进度',
				width : 100,
				dataIndex : '',
				sortable : false,
				fixed : true,
				renderer : this.formatProgressBar,
				align : 'center'
			}, {
				header : '&nbsp;',
				width : 28,
				dataIndex : 'fileState',
				renderer : this.formatState,
				sortable : false,
				fixed : true,
				align : 'center'
			}],
			autoExpandColumn : autoExpandColumnId,
			ds : new Ext.data.SimpleStore({
				fields : ['fileId', 'fileName', 'fileSize', 'fileType', 'fileState']
			}),
			bbar : [this.progressBar],
			tbar : [{
				text : '添加文件',
				iconCls : 'db-icn-add'
			}, {
				text : '开始上传',
				iconCls : 'db-icn-upload_',
				handler : this.startUpload,
				scope : this
			}, {
				text : '停止上传',
				iconCls : 'db-icn-stop',
				handler : this.stopUpload,
				scope : this
			}, {
				text : '取消队列',
				iconCls : 'db-icn-cross',
				handler : this.cancelQueue,
				scope : this
			}, {
				text : '清空列表',
				iconCls : 'db-icn-trash',
				handler : this.clearList,
				scope : this
			}],
			listeners : {
				cellclick : {
					fn : function(grid, rowIndex, columnIndex, e) {
						if (columnIndex == 5) {
							var record = grid.getSelectionModel().getSelected();
							var fileId = record.data.fileId;
							var file = this.swfupload.getFile(fileId);
							if (file) {
								if (file.filestatus != SWFUpload.FILE_STATUS.CANCELLED) {
									this.swfupload.cancelUpload(fileId);
									if (record.data.fileState != SWFUpload.FILE_STATUS.CANCELLED) {
										record.set('fileState', SWFUpload.FILE_STATUS.CANCELLED);
										record.commit();
										this.onCancelQueue(fileId);
									}
								}
							}
						}
					},
					scope : this
				},
				render : {
					fn : function() {
						var grid = this.get(1).get(0);
						var em = grid.getTopToolbar().get(0).el.child('em');
						var placeHolderId = Ext.id();
						em.setStyle({
							position : 'relative',
							display : 'block'
						});
						em.createChild({
							tag : 'div',
							id : placeHolderId
						});
						var settings = {
							upload_url : this.uploadUrl,
							post_params : Ext.isEmpty(this.postParams) ? {} : this.postParams,
							flash_url : Ext.isEmpty(this.flashUrl)
									? 'http://www.swfupload.org/swfupload.swf'
									: this.flashUrl,
							file_post_name : Ext.isEmpty(this.filePostName) ? 'myUpload' : this.filePostName,
							file_size_limit : Ext.isEmpty(this.fileSize) ? '100 MB' : this.fileSize,
							file_types : Ext.isEmpty(this.fileTypes) ? '*.*' : this.fileTypes,
							file_types_description : this.fileTypesDescription,
							use_query_string : true,
							debug : false,
							button_width : '73',
							button_height : '20',
							button_placeholder_id : placeHolderId,
							button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
							button_cursor : SWFUpload.CURSOR.HAND,
							custom_settings : {
								scope_handler : this
							},
							file_queued_handler : this.onFileQueued,
							file_queue_error_handler : this.onFileQueueError,
							file_dialog_complete_handler : this.onFileDialogComplete,
							upload_start_handler : this.onUploadStart,
							upload_progress_handler : this.onUploadProgress,
							upload_error_handler : this.onUploadError,
							upload_success_handler : this.onUploadSuccess,
							upload_complete_handler : this.onUploadComplete
						};
						this.swfupload = new SWFUpload(settings);
						this.swfupload.uploadStopped = false;
						Ext.get(this.swfupload.movieName).setStyle({
							position : 'absolute',
							top : 0,
							left : -2
						});
						this.resizeProgressBar();
						this.on('resize', this.resizeProgressBar, this);
					},
					scope : this,
					delay : 100
				}
			}

		});
		UploadPanel.superclass.constructor.call(this, Ext.applyIf(config || {}, {
			layout : 'border',
			width : 500,
			height : 400,
			minWidth : 450,
			minHeight : 400,
			split : true,
			items : [this.uploadInfoPanel, {
				region : 'center',
				layout : 'fit',
				margins : '0 -1 -1 -1',
				items : [this.fileList]
			}]
		}));
	},
	resizeProgressBar : function() {
		this.progressBar.setWidth(this.el.getWidth() - 5);
	},
	startUpload : function() {
		if (this.swfupload) {
			this.swfupload.uploadStopped = false;
			var post_params = this.swfupload.settings.post_params;
			// console.log(post_params);
			// post_params.path = encodeURI(this.scope.currentPath + '\\');
			// this.swfupload.setPostParams(post_params);
			this.swfupload.startUpload();
		}
	},
	stopUpload : function() {
		if (this.swfupload) {
			this.swfupload.uploadStopped = true;
			this.swfupload.stopUpload();
		}
	},
	cancelQueue : function() {
		if (this.swfupload) {
			this.swfupload.stopUpload();
			var stats = this.swfupload.getStats();
			while (stats.files_queued > 0) {
				this.swfupload.cancelUpload();
				stats = this.swfupload.getStats();
			}
			this.fileList.getStore().each(function(record) {
				switch (record.data.fileState) {
					case SWFUpload.FILE_STATUS.QUEUED :
					case SWFUpload.FILE_STATUS.IN_PROGRESS :
						record.set('fileState', SWFUpload.FILE_STATUS.CANCELLED);
						record.commit();
						this.onCancelQueue(record.data.fileId);
						break;
					default :
						break;
				}
			}, this);
		}
	},
	clearList : function() {
		var store = this.fileList.getStore();
		store.each(function(record) {
			if (record.data.fileState != SWFUpload.FILE_STATUS.QUEUED
					&& record.data.fileState != SWFUpload.FILE_STATUS.IN_PROGRESS) {
				store.remove(record);
			}
		});
	},
	getProgressTemplate : function() {
		var tpl = new Ext.Template('<table class="upload-progress-table"><tbody>',
				'<tr><td class="upload-progress-label"><nobr>已上传数:</nobr></td>',
				'<td class="upload-progress-value"><nobr>{filesUploaded} / {filesTotal}</nobr></td>',
				'<td class="upload-progress-label"><nobr>上传状态:</nobr></td>',
				'<td class="upload-progress-value"><nobr>{bytesUploaded} / {bytesTotal}</nobr></td></tr>',
				'<tr><td class="upload-progress-label"><nobr>已用时间:</nobr></td>',
				'<td class="upload-progress-value"><nobr>{timeElapsed}</nobr></td>',
				'<td class="upload-progress-label"><nobr>剩余时间:</nobr></td>',
				'<td class="upload-progress-value"><nobr>{timeLeft}</nobr></td></tr>',
				'<tr><td class="upload-progress-label"><nobr>当前速度:</nobr></td>',
				'<td class="upload-progress-value"><nobr>{speedLast}</nobr></td>',
				'<td class="upload-progress-label"><nobr>平均速度:</nobr></td>',
				'<td class="upload-progress-value"><nobr>{speedAverage}</nobr></td></tr>', '</tbody></table>');
		tpl.compile();
		return tpl;
	},
	updateProgressInfo : function() {
		this.getProgressTemplate().overwrite(this.uploadInfoPanel.body, this.formatProgress(this.progressInfo));
	},
	formatProgress : function(info) {
		var r = {};
		r.filesUploaded = String.leftPad(info.filesUploaded, 3, '&nbsp;');
		r.filesTotal = info.filesTotal;
		r.bytesUploaded = String.leftPad(Ext.util.Format.fileSize(info.bytesUploaded), 6, '&#160;');
		r.bytesTotal = Ext.util.Format.fileSize(info.bytesTotal);
		r.timeElapsed = this.formatTime(info.timeElapsed);
		r.speedAverage = Ext.util.Format.fileSize(Math.ceil(1000 * info.bytesUploaded / info.timeElapsed)) + '/s';
		r.timeLeft = this.formatTime((info.bytesUploaded === 0) ? 0 : info.timeElapsed
				* (info.bytesTotal - info.bytesUploaded) / info.bytesUploaded);
		var caleSpeed = 1000 * info.lastBytes / info.lastElapsed;
		r.speedLast = Ext.util.Format.fileSize(caleSpeed < 0 ? 0 : caleSpeed) + '/s';
		var p = info.bytesUploaded / info.bytesTotal;
		p = p || 0;
		this.progressBar.updateProgress(p, "上传中 " + Math.ceil(p * 100) + " %");
		return r;
	},
	formatTime : function(milliseconds) {
		var seconds = parseInt(milliseconds / 1000, 10);
		var s = 0;
		var m = 0;
		var h = 0;
		if (3599 < seconds) {
			h = parseInt(seconds / 3600, 10);
			seconds -= h * 3600;
		}
		if (59 < seconds) {
			m = parseInt(seconds / 60, 10);
			seconds -= m * 60;
		}
		m = String.leftPad(m, 2, '0');
		h = String.leftPad(h, 2, '0');
		s = String.leftPad(seconds, 2, '0');
		return h + ':' + m + ':' + s;
	},
	formatFileSize : function(_v, celmeta, record) {
		return '<div id="fileSize_' + record.data.fileId + '">' + Ext.util.Format.fileSize(_v) + '</div>';
	},
	formatFileName : function(_v, cellmeta, record) {
		return '<div id="fileName_' + record.data.fileId + '">' + _v + '</div>';
	},
	formatIcon : function(_v, cellmeta, record) {
		var returnValue = '';
		var extensionName = _v.substring(1);
		var fileId = record.data.fileId;
		if (_v) {
			var css = '.db-ft-' + extensionName.toLowerCase() + '-small';
			if (Ext.isEmpty(Ext.util.CSS.getRule(css), true)) { // 判断样式是否存在
				returnValue = '<div id="fileType_' + fileId
						+ '" class="db-ft-unknown-small" style="height: 16px;background-repeat: no-repeat;">'
						+ '&nbsp;&nbsp;&nbsp;&nbsp;' + extensionName.toUpperCase() + '</div>';
			} else {
				returnValue = '<div id="fileType_' + fileId + '" class="db-ft-' + extensionName.toLowerCase()
						+ '-small" style="height: 16px;background-repeat: no-repeat;"/>&nbsp;&nbsp;&nbsp;&nbsp;'
						+ extensionName.toUpperCase();
				+'</div>';
			}
			return returnValue;
		}
		return '<div id="fileType_'
				+ fileId
				+ '" class="db-ft-unknown-small" style="height: 16px;background-repeat: no-repeat;"/>&nbsp;&nbsp;&nbsp;&nbsp;'
				+ extensionName.toUpperCase() + '</div>';
	},
	formatProgressBar : function(_v, cellmeta, record) {
		var returnValue = '';
		switch (record.data.fileState) {
			case SWFUpload.FILE_STATUS.COMPLETE :
				if (Ext.isIE) {
					returnValue = '<div class="x-progress-wrap" style="height: 18px">'
							+ '<div class="x-progress-inner">'
							+ '<div style="width: 100%;" class="x-progress-bar x-progress-text">' + '100 %'
					'</div>' + '</div>' + '</div>';
				} else {
					returnValue = '<div class="x-progress-wrap" style="height: 18px">'
							+ '<div class="x-progress-inner">' + '<div id="progressBar_' + record.data.fileId
							+ '" style="width: 100%;" class="x-progress-bar">' + '</div>' + '<div id="progressText_'
							+ record.data.fileId
							+ '" style="width: 100%;" class="x-progress-text x-progress-text-back" />100 %</div>'
					'</div>' + '</div>';
				}
				break;
			default :
				returnValue = '<div class="x-progress-wrap" style="height: 18px">' + '<div class="x-progress-inner">'
						+ '<div id="progressBar_' + record.data.fileId + '" style="width: 0%;" class="x-progress-bar">'
						+ '</div>' + '<div id="progressText_' + record.data.fileId
						+ '" style="width: 100%;" class="x-progress-text x-progress-text-back" />0 %</div>'
				'</div>' + '</div>';
				break;
		}
		return returnValue;
	},
	formatState : function(_v, cellmeta, record) {
		var returnValue = '';
		switch (_v) {
			case SWFUpload.FILE_STATUS.QUEUED :
				returnValue = '<span id="' + record.id + '"><div id="fileId_' + record.data.fileId
						+ '" class="ux-cell-icon-delete"/></span>';
				break;
			case SWFUpload.FILE_STATUS.CANCELLED :
				returnValue = '<span id="' + record.id + '"><div id="fileId_' + record.data.fileId
						+ '" class="ux-cell-icon-clear"/></span>';
				break;
			case SWFUpload.FILE_STATUS.COMPLETE :
				returnValue = '<span id="' + record.id + '"><div id="fileId_' + record.data.fileId
						+ '" class="ux-cell-icon-completed"/></span>';
				break;
			default :
				alert('没有设置图表状态');
				break;
		}
		return returnValue;
	},
	onClose : function() {
		this.close();
	},
	onCancelQueue : function(fileId) {
		Ext.getDom('fileName_' + fileId).className = 'ux-cell-color-gray';// 设置文字颜色为灰色
		Ext.getDom('fileSize_' + fileId).className = 'ux-cell-color-gray';
		Ext.DomHelper.applyStyles('fileType_' + fileId, 'font-style:italic;text-decoration: line-through;color:gray');
	},
	onFileQueued : function(file) {
		var thiz = this.customSettings.scope_handler;
		thiz.fileList.getStore().add(new UploadPanel.FileRecord({
			fileId : file.id,
			fileName : file.name,
			fileSize : file.size,
			fileType : file.type,
			fileState : file.filestatus
		}));
		thiz.progressInfo.filesTotal += 1;
		thiz.progressInfo.bytesTotal += file.size;
		thiz.updateProgressInfo();
	},
	onQueueError : function(file, errorCode, message) {
		var thiz = this.customSettings.scope_handler;
		try {
			if (errorCode != SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED) {
				thiz.progressInfo.filesTotal -= 1;
				thiz.progressInfo.bytesTotal -= file.size;
			}
			thiz.progressInfo.bytesUploaded -= fpg.getBytesCompleted();
			thiz.updateProgressInfo();
		} catch (ex) {
			this.debug(ex);
		}
	},
	onFileDialogComplete : function(selectedFilesCount, queuedFilesCount) {
		// alert("selectedFilesCount:" + selectedFilesCount + "
		// queuedFilesCount:" + queuedFilesCount );
	},
	onUploadStart : function(file) {
	},
	onUploadProgress : function(file, completeBytes, bytesTotal) {
		var percent = Math.ceil((completeBytes / bytesTotal) * 100);
		Ext.getDom('progressBar_' + file.id).style.width = percent + "%";
		Ext.getDom('progressText_' + file.id).innerHTML = percent + " %";

		var thiz = this.customSettings.scope_handler;
		var bytes_added = completeBytes - thiz.progressInfo.currentCompleteBytes;
		thiz.progressInfo.bytesUploaded += Math.abs(bytes_added < 0 ? 0 : bytes_added);
		thiz.progressInfo.currentCompleteBytes = completeBytes;
		if (thiz.progressInfo.lastUpdate) {
			thiz.progressInfo.lastElapsed = thiz.progressInfo.lastUpdate.getElapsed();
			thiz.progressInfo.timeElapsed += thiz.progressInfo.lastElapsed;
		}
		thiz.progressInfo.lastBytes = bytes_added;
		thiz.progressInfo.lastUpdate = new Date();
		thiz.updateProgressInfo();
	},
	onUploadError : function(file, errorCode, message) {
		var thiz = this.customSettings.scope_handler;
		switch (errorCode) {
			case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED :
				thiz.progressInfo.filesTotal -= 1;
				thiz.progressInfo.bytesTotal -= file.size;
				thiz.updateProgressInfo();
				break;
			case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED :
		}
	},
	onUploadSuccess : function(file, serverData) {
		var thiz = this.customSettings.scope_handler;
		if (Ext.util.JSON.decode(serverData).success) {
			var record = thiz.fileList.getStore().getById(Ext.getDom('fileId_' + file.id).parentNode.id);
			record.set('fileState', file.filestatus);
			record.commit();
		}
		thiz.progressInfo.filesUploaded += 1;
		thiz.updateProgressInfo();
	},
	onUploadComplete : function(file) {
		if (this.getStats().files_queued > 0 && this.uploadStopped == false) {
			this.startUpload();
		}
	}
});

UploadPanel.FileRecord = Ext.data.Record.create([{
	name : 'fileId'
}, {
	name : 'fileName'
}, {
	name : 'fileSize'
}, {
	name : 'fileType'
}, {
	name : 'fileState'
}]);

Ext.reg('uploadpanel', UploadPanel);

var swfobject=function(){var E="undefined",s="object",T="Shockwave Flash",X="ShockwaveFlash.ShockwaveFlash",r="application/x-shockwave-flash",S="SWFObjectExprInst",y="onreadystatechange",P=window,k=document,u=navigator,U=false,V=[i],p=[],O=[],J=[],m,R,F,C,K=false,a=false,o,H,n=true,N=function(){var ab=typeof k.getElementById!=E&&typeof k.getElementsByTagName!=E&&typeof k.createElement!=E,ai=u.userAgent.toLowerCase(),Z=u.platform.toLowerCase(),af=Z?/win/.test(Z):/win/.test(ai),ad=Z?/mac/.test(Z):/mac/.test(ai),ag=/webkit/.test(ai)?parseFloat(ai.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,Y=!+"\v1",ah=[0,0,0],ac=null;if(typeof u.plugins!=E&&typeof u.plugins[T]==s){ac=u.plugins[T].description;if(ac&&!(typeof u.mimeTypes!=E&&u.mimeTypes[r]&&!u.mimeTypes[r].enabledPlugin)){U=true;Y=false;ac=ac.replace(/^.*\s+(\S+\s+\S+$)/,"$1");ah[0]=parseInt(ac.replace(/^(.*)\..*$/,"$1"),10);ah[1]=parseInt(ac.replace(/^.*\.(.*)\s.*$/,"$1"),10);ah[2]=/[a-zA-Z]/.test(ac)?parseInt(ac.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0}}else{if(typeof P.ActiveXObject!=E){try{var ae=new ActiveXObject(X);if(ae){ac=ae.GetVariable("$version");if(ac){Y=true;ac=ac.split(" ")[1].split(",");ah=[parseInt(ac[0],10),parseInt(ac[1],10),parseInt(ac[2],10)]}}}catch(aa){}}}return{w3:ab,pv:ah,wk:ag,ie:Y,win:af,mac:ad}}(),l=function(){if(!N.w3){return}if((typeof k.readyState!=E&&k.readyState=="complete")||(typeof k.readyState==E&&(k.getElementsByTagName("body")[0]||k.body))){g()}if(!K){if(typeof k.addEventListener!=E){k.addEventListener("DOMContentLoaded",g,false)}if(N.ie&&N.win){k.attachEvent(y,function(){if(k.readyState=="complete"){k.detachEvent(y,arguments.callee);g()}});if(P==top){(function(){if(K){return}try{k.documentElement.doScroll("left")}catch(Y){setTimeout(arguments.callee,0);return}g()})()}}if(N.wk){(function(){if(K){return}if(!/loaded|complete/.test(k.readyState)){setTimeout(arguments.callee,0);return}g()})()}t(g)}}();function g(){if(K){return}try{var aa=k.getElementsByTagName("body")[0].appendChild(D("span"));aa.parentNode.removeChild(aa)}catch(ab){return}K=true;var Y=V.length;for(var Z=0;Z<Y;Z++){V[Z]()}}function L(Y){if(K){Y()}else{V[V.length]=Y}}function t(Z){if(typeof P.addEventListener!=E){P.addEventListener("load",Z,false)}else{if(typeof k.addEventListener!=E){k.addEventListener("load",Z,false)}else{if(typeof P.attachEvent!=E){j(P,"onload",Z)}else{if(typeof P.onload=="function"){var Y=P.onload;P.onload=function(){Y();Z()}}else{P.onload=Z}}}}}function i(){if(U){W()}else{I()}}function W(){var Y=k.getElementsByTagName("body")[0];var ab=D(s);ab.setAttribute("type",r);var aa=Y.appendChild(ab);if(aa){var Z=0;(function(){if(typeof aa.GetVariable!=E){var ac=aa.GetVariable("$version");if(ac){ac=ac.split(" ")[1].split(",");N.pv=[parseInt(ac[0],10),parseInt(ac[1],10),parseInt(ac[2],10)]}}else{if(Z<10){Z++;setTimeout(arguments.callee,10);return}}Y.removeChild(ab);aa=null;I()})()}else{I()}}function I(){var ah=p.length;if(ah>0){for(var ag=0;ag<ah;ag++){var Z=p[ag].id;var ac=p[ag].callbackFn;var ab={success:false,id:Z};if(N.pv[0]>0){var af=c(Z);if(af){if(G(p[ag].swfVersion)&&!(N.wk&&N.wk<312)){x(Z,true);if(ac){ab.success=true;ab.ref=A(Z);ac(ab)}}else{if(p[ag].expressInstall&&B()){var aj={};aj.data=p[ag].expressInstall;aj.width=af.getAttribute("width")||"0";aj.height=af.getAttribute("height")||"0";if(af.getAttribute("class")){aj.styleclass=af.getAttribute("class")}if(af.getAttribute("align")){aj.align=af.getAttribute("align")}var ai={};var Y=af.getElementsByTagName("param");var ad=Y.length;for(var ae=0;ae<ad;ae++){if(Y[ae].getAttribute("name").toLowerCase()!="movie"){ai[Y[ae].getAttribute("name")]=Y[ae].getAttribute("value")}}Q(aj,ai,Z,ac)}else{q(af);if(ac){ac(ab)}}}}}else{x(Z,true);if(ac){var aa=A(Z);if(aa&&typeof aa.SetVariable!=E){ab.success=true;ab.ref=aa}ac(ab)}}}}}function A(ab){var Y=null;var Z=c(ab);if(Z&&Z.nodeName=="OBJECT"){if(typeof Z.SetVariable!=E){Y=Z}else{var aa=Z.getElementsByTagName(s)[0];if(aa){Y=aa}}}return Y}function B(){return !a&&G("6.0.65")&&(N.win||N.mac)&&!(N.wk&&N.wk<312)}function Q(ab,ac,Y,aa){a=true;F=aa||null;C={success:false,id:Y};var af=c(Y);if(af){if(af.nodeName=="OBJECT"){m=h(af);R=null}else{m=af;R=Y}ab.id=S;if(typeof ab.width==E||(!/%$/.test(ab.width)&&parseInt(ab.width,10)<310)){ab.width="310"}if(typeof ab.height==E||(!/%$/.test(ab.height)&&parseInt(ab.height,10)<137)){ab.height="137"}k.title=k.title.slice(0,47)+" - Flash Player Installation";var ae=N.ie&&N.win?"ActiveX":"PlugIn",ad="MMredirectURL="+P.location.toString().replace(/&/g,"%26")+"&MMplayerType="+ae+"&MMdoctitle="+k.title;if(typeof ac.flashvars!=E){ac.flashvars+="&"+ad}else{ac.flashvars=ad}if(N.ie&&N.win&&af.readyState!=4){var Z=D("div");Y+="SWFObjectNew";Z.setAttribute("id",Y);af.parentNode.insertBefore(Z,af);af.style.display="none";(function(){if(af.readyState==4){af.parentNode.removeChild(af)}else{setTimeout(arguments.callee,10)}})()}v(ab,ac,Y)}}function q(Z){if(N.ie&&N.win&&Z.readyState!=4){var Y=D("div");Z.parentNode.insertBefore(Y,Z);Y.parentNode.replaceChild(h(Z),Y);Z.style.display="none";(function(){if(Z.readyState==4){Z.parentNode.removeChild(Z)}else{setTimeout(arguments.callee,10)}})()}else{Z.parentNode.replaceChild(h(Z),Z)}}function h(ad){var ab=D("div");if(N.win&&N.ie){ab.innerHTML=ad.innerHTML}else{var Z=ad.getElementsByTagName(s)[0];if(Z){var ae=Z.childNodes;if(ae){var Y=ae.length;for(var aa=0;aa<Y;aa++){if(!(ae[aa].nodeType==1&&ae[aa].nodeName=="PARAM")&&!(ae[aa].nodeType==8)){ab.appendChild(ae[aa].cloneNode(true))}}}}}return ab}function v(aj,ah,Z){var Y,ab=c(Z);if(N.wk&&N.wk<312){return Y}if(ab){if(typeof aj.id==E){aj.id=Z}if(N.ie&&N.win){var ai="";for(var af in aj){if(aj[af]!=Object.prototype[af]){if(af.toLowerCase()=="data"){ah.movie=aj[af]}else{if(af.toLowerCase()=="styleclass"){ai+=' class="'+aj[af]+'"'}else{if(af.toLowerCase()!="classid"){ai+=" "+af+'="'+aj[af]+'"'}}}}}var ag="";for(var ae in ah){if(ah[ae]!=Object.prototype[ae]){ag+='<param name="'+ae+'" value="'+ah[ae]+'" />'}}ab.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+ai+">"+ag+"</object>";O[O.length]=aj.id;Y=c(aj.id)}else{var aa=D(s);aa.setAttribute("type",r);for(var ad in aj){if(aj[ad]!=Object.prototype[ad]){if(ad.toLowerCase()=="styleclass"){aa.setAttribute("class",aj[ad])}else{if(ad.toLowerCase()!="classid"){aa.setAttribute(ad,aj[ad])}}}}for(var ac in ah){if(ah[ac]!=Object.prototype[ac]&&ac.toLowerCase()!="movie"){e(aa,ac,ah[ac])}}ab.parentNode.replaceChild(aa,ab);Y=aa}}return Y}function e(aa,Y,Z){var ab=D("param");ab.setAttribute("name",Y);ab.setAttribute("value",Z);aa.appendChild(ab)}function z(Z){var Y=c(Z);if(Y&&Y.nodeName=="OBJECT"){if(N.ie&&N.win){Y.style.display="none";(function(){if(Y.readyState==4){b(Z)}else{setTimeout(arguments.callee,10)}})()}else{Y.parentNode.removeChild(Y)}}}function b(aa){var Z=c(aa);if(Z){for(var Y in Z){if(typeof Z[Y]=="function"){Z[Y]=null}}Z.parentNode.removeChild(Z)}}function c(aa){var Y=null;try{Y=k.getElementById(aa)}catch(Z){}return Y}function D(Y){return k.createElement(Y)}function j(aa,Y,Z){aa.attachEvent(Y,Z);J[J.length]=[aa,Y,Z]}function G(aa){var Z=N.pv,Y=aa.split(".");Y[0]=parseInt(Y[0],10);Y[1]=parseInt(Y[1],10)||0;Y[2]=parseInt(Y[2],10)||0;return(Z[0]>Y[0]||(Z[0]==Y[0]&&Z[1]>Y[1])||(Z[0]==Y[0]&&Z[1]==Y[1]&&Z[2]>=Y[2]))?true:false}function w(ad,Z,ae,ac){if(N.ie&&N.mac){return}var ab=k.getElementsByTagName("head")[0];if(!ab){return}var Y=(ae&&typeof ae=="string")?ae:"screen";if(ac){o=null;H=null}if(!o||H!=Y){var aa=D("style");aa.setAttribute("type","text/css");aa.setAttribute("media",Y);o=ab.appendChild(aa);if(N.ie&&N.win&&typeof k.styleSheets!=E&&k.styleSheets.length>0){o=k.styleSheets[k.styleSheets.length-1]}H=Y}if(N.ie&&N.win){if(o&&typeof o.addRule==s){o.addRule(ad,Z)}}else{if(o&&typeof k.createTextNode!=E){o.appendChild(k.createTextNode(ad+" {"+Z+"}"))}}}function x(aa,Y){if(!n){return}var Z=Y?"visible":"hidden";if(K&&c(aa)){c(aa).style.visibility=Z}else{w("#"+aa,"visibility:"+Z)}}function M(Z){var aa=/[\\\"<>\.;]/;var Y=aa.exec(Z)!=null;return Y&&typeof encodeURIComponent!=E?encodeURIComponent(Z):Z}var d=function(){if(N.ie&&N.win){window.attachEvent("onunload",function(){var ad=J.length;for(var ac=0;ac<ad;ac++){J[ac][0].detachEvent(J[ac][1],J[ac][2])}var aa=O.length;for(var ab=0;ab<aa;ab++){z(O[ab])}for(var Z in N){N[Z]=null}N=null;for(var Y in swfobject){swfobject[Y]=null}swfobject=null})}}();return{registerObject:function(ac,Y,ab,aa){if(N.w3&&ac&&Y){var Z={};Z.id=ac;Z.swfVersion=Y;Z.expressInstall=ab;Z.callbackFn=aa;p[p.length]=Z;x(ac,false)}else{if(aa){aa({success:false,id:ac})}}},getObjectById:function(Y){if(N.w3){return A(Y)}},embedSWF:function(ac,ai,af,ah,Z,ab,aa,ae,ag,ad){var Y={success:false,id:ai};if(N.w3&&!(N.wk&&N.wk<312)&&ac&&ai&&af&&ah&&Z){x(ai,false);L(function(){af+="";ah+="";var ak={};if(ag&&typeof ag===s){for(var am in ag){ak[am]=ag[am]}}ak.data=ac;ak.width=af;ak.height=ah;var an={};if(ae&&typeof ae===s){for(var al in ae){an[al]=ae[al]}}if(aa&&typeof aa===s){for(var aj in aa){if(typeof an.flashvars!=E){an.flashvars+="&"+aj+"="+aa[aj]}else{an.flashvars=aj+"="+aa[aj]}}}if(G(Z)){var ao=v(ak,an,ai);if(ak.id==ai){x(ai,true)}Y.success=true;Y.ref=ao}else{if(ab&&B()){ak.data=ab;Q(ak,an,ai,ad);return}else{x(ai,true)}}if(ad){ad(Y)}})}else{if(ad){ad(Y)}}},switchOffAutoHideShow:function(){n=false},ua:N,getFlashPlayerVersion:function(){return{major:N.pv[0],minor:N.pv[1],release:N.pv[2]}},hasFlashPlayerVersion:G,createSWF:function(aa,Z,Y){if(N.w3){return v(aa,Z,Y)}else{return undefined}},showExpressInstall:function(aa,ab,Y,Z){if(N.w3&&B()){Q(aa,ab,Y,Z)}},removeSWF:function(Y){if(N.w3){z(Y)}},createCSS:function(ab,aa,Z,Y){if(N.w3){w(ab,aa,Z,Y)}},addDomLoadEvent:L,addLoadEvent:t,getQueryParamValue:function(ab){var aa=k.location.search||k.location.hash;if(aa){if(/\?/.test(aa)){aa=aa.split("?")[1]}if(ab==null){return M(aa)}var Z=aa.split("&");for(var Y=0;Y<Z.length;Y++){if(Z[Y].substring(0,Z[Y].indexOf("="))==ab){return M(Z[Y].substring((Z[Y].indexOf("=")+1)))}}}return""},expressInstallCallback:function(){if(a){var Y=c(S);if(Y&&m){Y.parentNode.replaceChild(m,Y);if(R){x(R,true);if(N.ie&&N.win){m.style.display="block"}}if(F){F(C)}}a=false}}}}();Ext.FlashComponent=Ext.extend(Ext.BoxComponent,{flashVersion:"9.0.45",backgroundColor:"#ffffff",wmode:"opaque",flashVars:undefined,flashParams:undefined,url:undefined,swfId:undefined,swfWidth:"100%",swfHeight:"100%",expressInstall:false,initComponent:function(){Ext.FlashComponent.superclass.initComponent.call(this);this.addEvents("initialize")},onRender:function(){Ext.FlashComponent.superclass.onRender.apply(this,arguments);var b=Ext.apply({allowScriptAccess:"always",bgcolor:this.backgroundColor,wmode:this.wmode},this.flashParams),a=Ext.apply({allowedDomain:document.location.hostname,elementID:this.getId(),eventHandler:"Ext.FlashEventProxy.onEvent"},this.flashVars);new swfobject.embedSWF(this.url,this.id,this.swfWidth,this.swfHeight,this.flashVersion,this.expressInstall?Ext.FlashComponent.EXPRESS_INSTALL_URL:undefined,a,b);this.swf=Ext.getDom(this.id);this.el=Ext.get(this.swf)},getSwfId:function(){return this.swfId||(this.swfId="extswf"+(++Ext.Component.AUTO_ID))},getId:function(){return this.id||(this.id="extflashcmp"+(++Ext.Component.AUTO_ID))},onFlashEvent:function(a){switch(a.type){case"swfReady":this.initSwf();return;case"log":return}a.component=this;this.fireEvent(a.type.toLowerCase().replace(/event$/,""),a)},initSwf:function(){this.onSwfReady(!!this.isInitialized);this.isInitialized=true;this.fireEvent("initialize",this)},beforeDestroy:function(){if(this.rendered){swfobject.removeSWF(this.swf.id)}Ext.FlashComponent.superclass.beforeDestroy.call(this)},onSwfReady:Ext.emptyFn});Ext.FlashComponent.EXPRESS_INSTALL_URL="http://swfobject.googlecode.com/svn/trunk/swfobject/expressInstall.swf";Ext.reg("flash",Ext.FlashComponent);Ext.FlashEventProxy={onEvent:function(c,b){var a=Ext.getCmp(c);if(a){a.onFlashEvent(b)}else{arguments.callee.defer(10,this,[c,b])}}};

Date.prototype.getElapsed = function(A) {
	return Math.abs((A || new Date()).getTime() - this.getTime())
};

