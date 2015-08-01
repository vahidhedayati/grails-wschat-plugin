/*
 * Copyright 2010, Wen Pu (dexterpu at gmail dot com)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * Check out http://www.cs.illinois.edu/homes/wenpu1/chatbox.html  for document
 *
 * Depends on jquery.ui.core, jquery.ui.widiget, jquery.ui.effect
 *
 * Also uses some styles for jquery.ui.dialog
 * 
 * 
 * This is a slightly modified version of chatbox 
 * Vahid Hedayati  September 2014.
 * This allows additional video call within a webpage that currently
 * has chatbox widgets.
 * There are overlapping issues and for now a 
 * hack exists in wschat.js around like 679.
 * labelled as: jquery.ui.chatbox VS jquery.ui.videobox nasty hack
 *
 */

//TODO: implement destroy()
(function($) {
	//contentLoaders2 =
	

	
	$.widget("ui.videobox", {
		options: {
			id: null, //id for the DOM element
			title: null, // title of the videobox
			user: null, // can be anything associated with this videobox
			sender: null,
			camaction: null,
			viewtype: null,
			hidden: false,
			offset: 0, // relative to right edge of the browser window
			width: 300, // width of the videobox
			vidSent: function(id, sender) {
				// override this
				this.vidManager.vidMsg(sender);
			},
			boxClosed: function(id) {
			}, // called when the close icon is clicked
			vidManager: {
				// thanks to the widget factory facility
				// similar to http://alexsexton.com/?p=51
				init: function(elem) {
					this.elem = elem;
				},
				vidMsg: function(peer) {
					var self = this;
					var vidbox = self.elem.uiVidboxLog;
					var e = document.createElement('div');
					vidbox.append(e);
					$(e).hide();


					if (!self.elem.uiVidboxTitlebar.hasClass("ui-state-focus")) { 
						//	&& !self.highlightLock) {
						//self.highlightLock = true;
						//self.highlightBox();
					}
				},
				closeBox: function() {
					this.elem.uiVidbox.hide();
					this.elem.uiVidbox.remove();
					var cuser = $(self.element).attr("id");
					delCamList(cuser);
				},
				toggleBox: function() {
					this.elem.uiVidbox.toggle();
				}
			}
		},
		toggleContent: function(event) {
			this.uiVidboxContent.toggle();
			if (this.uiVidboxContent.is(":visible")) {
				//this.uiVidboxInputBox.focus();
			}
		},
		widget: function() {
			return this.uiVidbox
		},
		_create: function() {
			var self = this,
			options = self.options,
			title = options.title || "No Title",
			user = options.user,
			sender = options.sender,
			camaction = options.camaction,
			viewtype = options.viewtype
			// videobox
			var uividContent

			// Webcam old method
			if (viewtype=="webcam") { 
				if (camaction=="view") {
					uiVidContent='<div id="camViewContainer"></div>'+getCam(sender);
				}else{
					uiVidContent='<div id="myCamContainer"></div>'+sendCam();
				}
				//webrtc new method
			} else if (viewtype=="game") { 
				if (camaction=="view") {
					uiVidContent='<div id="camViewContainer"></div>'+getGame(sender);
				}else{
					uiVidContent='<div id="myCamContainer"></div>'+sendGame();
				}	
			} else if (viewtype=="fileshare") { 
				if (camaction=="view") {
					uiVidContent='<div id="camViewContainer"></div>'+getFile(sender);
				}else{
					uiVidContent='<div id="myCamContainer"></div>'+sendFile();
				}
			} else if (viewtype=="mediastream") { 
				if (camaction=="view") {
					uiVidContent='<div id="camViewContainer"></div>'+getMedia(sender);
				}else{
					uiVidContent='<div id="myCamContainer"></div>'+sendMedia();
				}
			}else{
				if (camaction=="view") {
					uiVidContent='<div id="camViewContainer"></div>'+getWebrtc(sender,viewtype);
				}else{
					uiVidContent='<div id="myCamContainer"></div>'+sendWebrtc(viewtype);
				}		
			}

			uiVidbox = (self.uiVidbox = $('<div></div>'))
			.appendTo(document.body)
			.addClass('ui-widget ' +'ui-corner-top ' +'ui-videobox')
			.attr('outline', 0)

			////.focusin(function() {
				// ui-state-highlight is not really helpful here
				//self.uiVidbox.removeClass('ui-state-highlight');
			////	self.uiVidboxTitlebar.addClass('ui-state-focus');
			////})

			////.focusout(function() {
			////	self.uiVidboxTitlebar.removeClass('ui-state-focus');
			///}),

			// titlebar
			uiVidboxTitlebar = (self.uiVidboxTitlebar = $('<div></div>'))
			.addClass('ui-widget-header ' +	'ui-corner-top ' +'ui-videobox-titlebar ' +'ui-dialog-header')
			.click(function(event) {
				self.toggleContent(event);
			})
			.appendTo(uiVidbox),

			uiVidboxTitle = (self.uiVidboxTitle = $('<span></span>'))
			.html(title)
			.appendTo(uiVidboxTitlebar),

			uiVidboxTitlebarClose = (self.uiVidboxTitlebarClose = $('<a id="closeroom" href=""></a>'))
			.addClass('ui-corner-all ' +'ui-videobox-icon ')
			.attr('role', 'button')
			.hover(
					function() { uiVidboxTitlebarClose.addClass('ui-state-hover'); },
					function() { uiVidboxTitlebarClose.removeClass('ui-state-hover'); }
			)
			.click(function(event) {
				var cuser = $(self.element).attr("id");
				if (cuser==user+'_webcam') {
					if (viewtype=="webcam") {
						disableAV();
						WebCam.close();
					}else{
						disablertc();
						WebRTC.close();
					}
				}
				delCamList(cuser);
				uiVidbox.hide();
				uiVidbox.remove();
				self.options.boxClosed(self.options.id);
				return false;
			})
			.appendTo(uiVidboxTitlebar),

			uiVidboxTitlebarCloseText = $('<span></span>')
			.addClass('ui-icon ' +'ui-icon-closethick')
			.text('close')
			.appendTo(uiVidboxTitlebarClose),

			uiVidboxTitlebarMinimize = (self.uiVidboxTitlebarMinimize = $('<a href="#"></a>'))
			.addClass('ui-corner-all ' +'ui-videobox-icon')
			.attr('role', 'button')
			.hover(
					function() { uiVidboxTitlebarMinimize.addClass('ui-state-hover'); },
					function() { uiVidboxTitlebarMinimize.removeClass('ui-state-hover'); }
			)
			.click(function(event) {
				self.toggleContent(event);
				return false;
			})
			.appendTo(uiVidboxTitlebar),

			uiVidboxTitlebarMinimizeText = $('<span></span>')
			.addClass('ui-icon ' +'ui-icon-minusthick')
			.text('minimize')
			.appendTo(uiVidboxTitlebarMinimize),


			// Set according to cam request
			// defined by uiVidContent
			uiVidboxContent = (
					self.uiVidboxContent = $(uiVidContent)
			)
			.addClass('ui-widget-content ' +'ui-videobox-content ')
			.appendTo(uiVidbox),

			uiVidboxLog = (self.uiVidboxLog = self.element)
			.addClass('ui-widget-content ' +'ui-videobox-log')
			.appendTo(uiVidboxContent)
			////.focusin(function() {
				//uiVidboxInputBox.addClass('ui-videobox-input-focus');
				//var vidbox = $(this).parent().prev();
				//vidbox.scrollTop(vidbox.get(0).scrollHeight);
			////})
			////.focusout(function() {
			////	uiVidboxInputBox.removeClass('ui-videobox-input-focus');
			////});			
			//// disable selection
			uiVidboxTitlebar.find('*').add(uiVidboxTitlebar).disableSelection();
			self._setWidth(self.options.width);
			self._position(self.options.offset);
			self.options.vidManager.init(self);
			//if (!self.options.hidden) {
			//	uiVidbox.show();
			//}
		},
		_setOption: function(option, value) {
			if (value != null) {
				switch (option) {
				case "hidden":
					if (value)
						this.uiVidbox.hide();
					else
						this.uiVidbox.show();
					break;
				case "show":
					this.uiVidbox.show();
					break;
				case "offset":
					this._position(value);
					break;
				case "width":
					this._setWidth(value);
					break;
				}
			}
			$.Widget.prototype._setOption.apply(this, arguments);
		},
		_setWidth: function(width) {
			this.uiVidboxTitlebar.width(width + "px");
			this.uiVidboxLog.width(width + "px");

		},
		_position: function(offset) {
			this.uiVidbox.css("right", offset);
		}
	});
	//var contentLoaders = [];
	//contentLoaders.push(contentLoader2);
}(jQuery));

