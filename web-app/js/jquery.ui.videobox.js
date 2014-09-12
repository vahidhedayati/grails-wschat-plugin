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
				this.boxManager.vidMsg(user.first_name, msg);
			},
			boxClosed: function(id) {
			}, // called when the close icon is clicked
			vidManager: {
				// thanks to the widget factory facility
				// similar to http://alexsexton.com/?p=51
				init: function(elem) {
					this.elem = elem;
				},
				vidMsg: function(peer, msg) {
					var self = this;
					var box = self.elem.uiVidboxLog;
					var e = document.createElement('div');
					box.append(e);
					$(e).hide();
					/*
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
					*/
					if (!self.elem.uiVidboxTitlebar.hasClass("ui-state-focus")
							&& !self.highlightLock) {
						self.highlightLock = true;
						self.highlightBox();
					}
				},
				highlightBox: function() {
					var self = this;
					self.elem.uiVidboxTitlebar.effect("highlight", {}, 300);
					self.elem.uiVidbox.effect("bounce", {times: 3}, 300, function() {
						self.highlightLock = false;
						self._scrollToBottom();
					});
				},
				toggleBox: function() {
					this.elem.uiVidbox.toggle();
				},
				_scrollToBottom: function() {
					var box = this.elem.uiVidboxLog;
					box.scrollTop(box.get(0).scrollHeight);
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
			sender = options.sender,
			camaction = options.camaction
			// videobox
			if (camaction=="view") {

				uiVidbox = (self.uiVidbox = $('<div></div>'))
				.appendTo(document.body)
				.addClass('ui-widget ' +
						'ui-corner-top ' +
						'ui-videobox'
				)
				.attr('outline', 0)
				.focusin(function() {
					// ui-state-highlight is not really helpful here
					//self.uiVidbox.removeClass('ui-state-highlight');
					self.uiVidboxTitlebar.addClass('ui-state-focus');
				})
				.focusout(function() {
					self.uiVidboxTitlebar.removeClass('ui-state-focus');
				}),
				// titlebar
				uiVidboxTitlebar = (self.uiVidboxTitlebar = $('<div></div>'))
				.addClass('ui-widget-header ' +
						'ui-corner-top ' +
						'ui-videobox-titlebar ' +
						'ui-dialog-header' // take advantage of dialog header style
				)
				.click(function(event) {
					self.toggleContent(event);
				})
				.appendTo(uiVidbox),
				uiVidboxTitle = (self.uiVidboxTitle = $('<span></span>'))
				.html(title)
				.appendTo(uiVidboxTitlebar),
				uiVidboxTitlebarClose = (self.uiVidboxTitlebarClose = $('<a href="#"></a>'))
				.addClass('ui-corner-all ' +
						'ui-videobox-icon '
				)
				.attr('role', 'button')
				.hover(function() { uiVidboxTitlebarClose.addClass('ui-state-hover'); },
						function() { uiVidboxTitlebarClose.removeClass('ui-state-hover'); })
						.click(function(event) {
							uiVidbox.hide();
							self.options.boxClosed(self.options.id);
							return false;
						})
						.appendTo(uiVidboxTitlebar),
						uiVidboxTitlebarCloseText = $('<span></span>')
						.addClass('ui-icon ' +
						'ui-icon-closethick')
						.text('close')
						.appendTo(uiVidboxTitlebarClose),
						uiVidboxTitlebarMinimize = (self.uiVidboxTitlebarMinimize = $('<a href="#"></a>'))
						.addClass('ui-corner-all ' +
								'ui-videobox-icon'
						)
						.attr('role', 'button')
						.hover(function() { uiVidboxTitlebarMinimize.addClass('ui-state-hover'); },
								function() { uiVidboxTitlebarMinimize.removeClass('ui-state-hover'); })
								.click(function(event) {
									self.toggleContent(event);
									return false;
								})
								.appendTo(uiVidboxTitlebar),

								uiVidboxTitlebarMinimizeText = $('<span></span>')
								.addClass('ui-icon ' +
								'ui-icon-minusthick')
								.text('minimize')
								.appendTo(uiVidboxTitlebarMinimize),
								// content
								//uiVidboxContent = (self.uiVidboxContent = $('<video id="live" width="320" height="240" autoplay="autoplay"  style="display: inline;"></video>\n<canvas width="320" id="canvas" height="240" style="display: inline;"></canvas>'+getCam2()))

								uiVidboxContent = (self.uiVidboxContent = $('<div id="cam'+sender+'ViewContainer"></div>'+getCam(sender)))
								.addClass('ui-widget-content ' +
										'ui-videobox-content '
								)
								.appendTo(uiVidbox),
								uiVidboxLog = (self.uiVidboxLog = self.element)
								.addClass('ui-widget-content ' +
										'ui-videobox-log'
								)
								.appendTo(uiVidboxContent)

								.focusin(function() {
									uiVidboxInputBox.addClass('ui-videobox-input-focus');
									var box = $(this).parent().prev();
									box.scrollTop(box.get(0).scrollHeight);
								})
								.focusout(function() {
									uiVidboxInputBox.removeClass('ui-videobox-input-focus');
								});
			}else{
				uiVidbox = (self.uiVidbox = $('<div></div>'))
				.appendTo(document.body)
				.addClass('ui-widget ' +
						'ui-corner-top ' +
						'ui-videobox'
				)
				.attr('outline', 0)
				.focusin(function() {
					// ui-state-highlight is not really helpful here
					//self.uiVidbox.removeClass('ui-state-highlight');
					self.uiVidboxTitlebar.addClass('ui-state-focus');
				})
				.focusout(function() {
					self.uiVidboxTitlebar.removeClass('ui-state-focus');
				}),
				// titlebar
				uiVidboxTitlebar = (self.uiVidboxTitlebar = $('<div></div>'))
				.addClass('ui-widget-header ' +
						'ui-corner-top ' +
						'ui-videobox-titlebar ' +
						'ui-dialog-header' // take advantage of dialog header style
				)
				.click(function(event) {
					self.toggleContent(event);
				})
				.appendTo(uiVidbox),
				uiVidboxTitle = (self.uiVidboxTitle = $('<span></span>'))
				.html(title)
				.appendTo(uiVidboxTitlebar),
				uiVidboxTitlebarClose = (self.uiVidboxTitlebarClose = $('<a href="#"></a>'))
				.addClass('ui-corner-all ' +
						'ui-videobox-icon '
				)
				.attr('role', 'button')
				.hover(function() { uiVidboxTitlebarClose.addClass('ui-state-hover'); },
						function() { uiVidboxTitlebarClose.removeClass('ui-state-hover'); })
						.click(function(event) {
							uiVidbox.hide();
							disableAV();
							self.options.boxClosed(self.options.id);
							return false;
						})
						.appendTo(uiVidboxTitlebar),
						uiVidboxTitlebarCloseText = $('<span></span>')
						.addClass('ui-icon ' +
								'ui-icon-closethick')
								.text('close')
								.appendTo(uiVidboxTitlebarClose),
								uiVidboxTitlebarMinimize = (self.uiVidboxTitlebarMinimize = $('<a href="#"></a>'))
								.addClass('ui-corner-all ' +
										'ui-videobox-icon'
								)
								.attr('role', 'button')
								.hover(function() { uiVidboxTitlebarMinimize.addClass('ui-state-hover'); },
										function() { uiVidboxTitlebarMinimize.removeClass('ui-state-hover'); })
										.click(function(event) {
											self.toggleContent(event);
											return false;
										})
										.appendTo(uiVidboxTitlebar),

										uiVidboxTitlebarMinimizeText = $('<span></span>')
										.addClass('ui-icon ' +
										'ui-icon-minusthick')
										.text('minimize')
										.appendTo(uiVidboxTitlebarMinimize),
										// content
										//uiVidboxContent = (self.uiVidboxContent = $('<video id="live" width="320" height="240" autoplay="autoplay"  style="display: inline;"></video>\n<canvas width="320" id="canvas" height="240" style="display: inline;"></canvas>'+getCam2()))

										uiVidboxContent = (self.uiVidboxContent = $('<div id="cam'+user+'Container"></div>'+sendCam())                 )
										.addClass('ui-widget-content ' +
												'ui-videobox-content '
										)
										.appendTo(uiVidbox),
										uiVidboxLog = (self.uiVidboxLog = self.element)
										.addClass('ui-widget-content ' +
												'ui-videobox-log'
										)
										.appendTo(uiVidboxContent)

										.focusin(function() {
											uiVidboxInputBox.addClass('ui-videobox-input-focus');
											var box = $(this).parent().prev();
											box.scrollTop(box.get(0).scrollHeight);
										})
										.focusout(function() {
											uiVidboxInputBox.removeClass('ui-videobox-input-focus');
										});
			}
			// disable selection
			uiVidboxTitlebar.find('*').add(uiVidboxTitlebar).disableSelection();

			/*
            // switch focus to input box when whatever clicked
            uiVidboxContent.children().click(function() {
                // click on any children, set focus on input box
                self.uiVidboxInputBox.focus();
            });
			 */

			self._setWidth(self.options.width);
			self._position(self.options.offset);

			self.options.boxManager.init(self);

			if (!self.options.hidden) {
				uiVidbox.show();
			}
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
}(jQuery));
