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
 */


//TODO: implement destroy()
(function($) {
	$.widget("ui.videobox", {
		options: {
			id: null, //id for the DOM element
			title: null, // title of the videobox
			user: null, // can be anything associated with this videobox
			sender: null,
			camaction: null,
			hidden: false,
			offset: 0, // relative to right edge of the browser window
			width: 300, // width of the videobox
			messageSent: function(id, user, msg) {
				// override this
				this.boxManager.addMsg(user.first_name, msg);
			},
			boxClosed: function(id) {
			}, // called when the close icon is clicked
			boxManager: {
				// thanks to the widget factory facility
				// similar to http://alexsexton.com/?p=51
				init: function(elem) {
					this.elem = elem;
				},
				addMsg: function(peer, msg) {
					var self = this;
					var box = self.elem.uiChatboxLog;
					var e = document.createElement('div');
					box.append(e);
					$(e).hide();

					var systemMessage = false;

					if (peer) {
						var peerName = document.createElement("b");
						$(peerName).text(peer + ": ");
						e.appendChild(peerName);
					} else {
						systemMessage = true;
					}

					var msgElement = document.createElement(
							systemMessage ? "i" : "span");
					$(msgElement).text(msg);
					e.appendChild(msgElement);
					$(e).addClass("ui-videobox-msg");
					$(e).css("maxWidth", $(box).width());
					$(e).fadeIn();
					self._scrollToBottom();

					if (!self.elem.uiChatboxTitlebar.hasClass("ui-state-focus")
							&& !self.highlightLock) {
						self.highlightLock = true;
						self.highlightBox();
					}
				},
				highlightBox: function() {
					var self = this;
					self.elem.uiChatboxTitlebar.effect("highlight", {}, 300);
					self.elem.uiChatbox.effect("bounce", {times: 3}, 300, function() {
						self.highlightLock = false;
						self._scrollToBottom();
					});
				},
				toggleBox: function() {
					this.elem.uiChatbox.toggle();
				},
				_scrollToBottom: function() {
					var box = this.elem.uiChatboxLog;
					box.scrollTop(box.get(0).scrollHeight);
				}
			}
		},
		toggleContent: function(event) {
			this.uiChatboxContent.toggle();
			if (this.uiChatboxContent.is(":visible")) {
				//this.uiChatboxInputBox.focus();
			}
		},
		widget: function() {
			return this.uiChatbox
		},
		_create: function() {
			var self = this,
			options = self.options,
			title = options.title || "No Title",
			sender = options.sender,
			camaction = options.camaction
			// videobox
			if (camaction=="view") {

				uiChatbox = (self.uiChatbox = $('<div></div>'))
				.appendTo(document.body)
				.addClass('ui-widget ' +
						'ui-corner-top ' +
						'ui-videobox'
				)
				.attr('outline', 0)
				.focusin(function() {
					// ui-state-highlight is not really helpful here
					//self.uiChatbox.removeClass('ui-state-highlight');
					self.uiChatboxTitlebar.addClass('ui-state-focus');
				})
				.focusout(function() {
					self.uiChatboxTitlebar.removeClass('ui-state-focus');
				}),
				// titlebar
				uiChatboxTitlebar = (self.uiChatboxTitlebar = $('<div></div>'))
				.addClass('ui-widget-header ' +
						'ui-corner-top ' +
						'ui-videobox-titlebar ' +
						'ui-dialog-header' // take advantage of dialog header style
				)
				.click(function(event) {
					self.toggleContent(event);
				})
				.appendTo(uiChatbox),
				uiChatboxTitle = (self.uiChatboxTitle = $('<span></span>'))
				.html(title)
				.appendTo(uiChatboxTitlebar),
				uiChatboxTitlebarClose = (self.uiChatboxTitlebarClose = $('<a href="#"></a>'))
				.addClass('ui-corner-all ' +
						'ui-videobox-icon '
				)
				.attr('role', 'button')
				.hover(function() { uiChatboxTitlebarClose.addClass('ui-state-hover'); },
						function() { uiChatboxTitlebarClose.removeClass('ui-state-hover'); })
						.click(function(event) {
							uiChatbox.hide();
							self.options.boxClosed(self.options.id);
							return false;
						})
						.appendTo(uiChatboxTitlebar),
						uiChatboxTitlebarCloseText = $('<span></span>')
						.addClass('ui-icon ' +
						'ui-icon-closethick')
						.text('close')
						.appendTo(uiChatboxTitlebarClose),
						uiChatboxTitlebarMinimize = (self.uiChatboxTitlebarMinimize = $('<a href="#"></a>'))
						.addClass('ui-corner-all ' +
								'ui-videobox-icon'
						)
						.attr('role', 'button')
						.hover(function() { uiChatboxTitlebarMinimize.addClass('ui-state-hover'); },
								function() { uiChatboxTitlebarMinimize.removeClass('ui-state-hover'); })
								.click(function(event) {
									self.toggleContent(event);
									return false;
								})
								.appendTo(uiChatboxTitlebar),

								uiChatboxTitlebarMinimizeText = $('<span></span>')
								.addClass('ui-icon ' +
								'ui-icon-minusthick')
								.text('minimize')
								.appendTo(uiChatboxTitlebarMinimize),
								// content
								//uiChatboxContent = (self.uiChatboxContent = $('<video id="live" width="320" height="240" autoplay="autoplay"  style="display: inline;"></video>\n<canvas width="320" id="canvas" height="240" style="display: inline;"></canvas>'+getCam2()))

								uiChatboxContent = (self.uiChatboxContent = $('<div id="cam'+sender+'ViewContainer"></div>'+getCam(sender)))
								.addClass('ui-widget-content ' +
										'ui-videobox-content '
								)
								.appendTo(uiChatbox),
								uiChatboxLog = (self.uiChatboxLog = self.element)
								.addClass('ui-widget-content ' +
										'ui-videobox-log'
								)
								.appendTo(uiChatboxContent)

								.focusin(function() {
									uiChatboxInputBox.addClass('ui-videobox-input-focus');
									var box = $(this).parent().prev();
									box.scrollTop(box.get(0).scrollHeight);
								})
								.focusout(function() {
									uiChatboxInputBox.removeClass('ui-videobox-input-focus');
								});
			}else{
				uiChatbox = (self.uiChatbox = $('<div></div>'))
				.appendTo(document.body)
				.addClass('ui-widget ' +
						'ui-corner-top ' +
						'ui-videobox'
				)
				.attr('outline', 0)
				.focusin(function() {
					// ui-state-highlight is not really helpful here
					//self.uiChatbox.removeClass('ui-state-highlight');
					self.uiChatboxTitlebar.addClass('ui-state-focus');
				})
				.focusout(function() {
					self.uiChatboxTitlebar.removeClass('ui-state-focus');
				}),
				// titlebar
				uiChatboxTitlebar = (self.uiChatboxTitlebar = $('<div></div>'))
				.addClass('ui-widget-header ' +
						'ui-corner-top ' +
						'ui-videobox-titlebar ' +
						'ui-dialog-header' // take advantage of dialog header style
				)
				.click(function(event) {
					self.toggleContent(event);
				})
				.appendTo(uiChatbox),
				uiChatboxTitle = (self.uiChatboxTitle = $('<span></span>'))
				.html(title)
				.appendTo(uiChatboxTitlebar),
				uiChatboxTitlebarClose = (self.uiChatboxTitlebarClose = $('<a href="#"></a>'))
				.addClass('ui-corner-all ' +
						'ui-videobox-icon '
				)
				.attr('role', 'button')
				.hover(function() { uiChatboxTitlebarClose.addClass('ui-state-hover'); },
						function() { uiChatboxTitlebarClose.removeClass('ui-state-hover'); })
						.click(function(event) {
							uiChatbox.hide();
							disableAV();
							self.options.boxClosed(self.options.id);
							return false;
						})
						.appendTo(uiChatboxTitlebar),
						uiChatboxTitlebarCloseText = $('<span></span>')
						.addClass('ui-icon ' +
								'ui-icon-closethick')
								.text('close')
								.appendTo(uiChatboxTitlebarClose),
								uiChatboxTitlebarMinimize = (self.uiChatboxTitlebarMinimize = $('<a href="#"></a>'))
								.addClass('ui-corner-all ' +
										'ui-videobox-icon'
								)
								.attr('role', 'button')
								.hover(function() { uiChatboxTitlebarMinimize.addClass('ui-state-hover'); },
										function() { uiChatboxTitlebarMinimize.removeClass('ui-state-hover'); })
										.click(function(event) {
											self.toggleContent(event);
											return false;
										})
										.appendTo(uiChatboxTitlebar),

										uiChatboxTitlebarMinimizeText = $('<span></span>')
										.addClass('ui-icon ' +
										'ui-icon-minusthick')
										.text('minimize')
										.appendTo(uiChatboxTitlebarMinimize),
										// content
										//uiChatboxContent = (self.uiChatboxContent = $('<video id="live" width="320" height="240" autoplay="autoplay"  style="display: inline;"></video>\n<canvas width="320" id="canvas" height="240" style="display: inline;"></canvas>'+getCam2()))

										uiChatboxContent = (self.uiChatboxContent = $('<div id="cam'+user+'Container"></div>'+sendCam())                 )
										.addClass('ui-widget-content ' +
												'ui-videobox-content '
										)
										.appendTo(uiChatbox),
										uiChatboxLog = (self.uiChatboxLog = self.element)
										.addClass('ui-widget-content ' +
												'ui-videobox-log'
										)
										.appendTo(uiChatboxContent)

										.focusin(function() {
											uiChatboxInputBox.addClass('ui-videobox-input-focus');
											var box = $(this).parent().prev();
											box.scrollTop(box.get(0).scrollHeight);
										})
										.focusout(function() {
											uiChatboxInputBox.removeClass('ui-videobox-input-focus');
										});
			}
			// disable selection
			uiChatboxTitlebar.find('*').add(uiChatboxTitlebar).disableSelection();

			/*
            // switch focus to input box when whatever clicked
            uiChatboxContent.children().click(function() {
                // click on any children, set focus on input box
                self.uiChatboxInputBox.focus();
            });
			 */

			self._setWidth(self.options.width);
			self._position(self.options.offset);

			self.options.boxManager.init(self);

			if (!self.options.hidden) {
				uiChatbox.show();
			}
		},
		_setOption: function(option, value) {
			if (value != null) {
				switch (option) {
				case "hidden":
					if (value)
						this.uiChatbox.hide();
					else
						this.uiChatbox.show();
					break;
				case "show":
					this.uiChatbox.show();
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
			this.uiChatboxTitlebar.width(width + "px");
			this.uiChatboxLog.width(width + "px");

		},
		_position: function(offset) {
			this.uiChatbox.css("right", offset);
		}
	});
}(jQuery));
