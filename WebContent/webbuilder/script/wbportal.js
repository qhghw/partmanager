Ext.ux.Portal = Ext.extend(Ext.Panel, {
	layout : 'column',
	autoScroll : true,
	bodyStyle : 'overflow-x:hidden',
	cls : 'x-portal',
	defaultType : 'portalcolumn',
	initComponent : function() {
		Ext.ux.Portal.superclass.initComponent.call(this);
		this.addEvents( {
			validatedrop : true,
			beforedragover : true,
			dragover : true,
			beforedrop : true,
			drop : true
		});
	},
	initEvents : function() {
		Ext.ux.Portal.superclass.initEvents.call(this);
		this.dd = new Ext.ux.Portal.DropZone(this, this.dropConfig);
	},
	beforeDestroy : function() {
		if (this.dd) {
			this.dd.unreg();
		}
		Ext.ux.Portal.superclass.beforeDestroy.call(this);
	}
});
Ext.reg('portal', Ext.ux.Portal);
Ext.ux.Portal.DropZone = function(portal, cfg) {
	this.portal = portal;
	Ext.dd.ScrollManager.register(portal.body);
	Ext.ux.Portal.DropZone.superclass.constructor.call(this, portal.bwrap.dom,
			cfg);
	portal.body.ddScrollConfig = this.ddScrollConfig;
};
Ext
		.extend(
				Ext.ux.Portal.DropZone,
				Ext.dd.DropTarget,
				{
					ddScrollConfig : {
						vthresh : 50,
						hthresh : -1,
						animate : true,
						increment : 200
					},
					createEvent : function(dd, e, data, col, c, pos) {
						return {
							portal : this.portal,
							panel : data.panel,
							columnIndex : col,
							column : c,
							position : pos,
							data : data,
							source : dd,
							rawEvent : e,
							status : this.dropAllowed
						};
					},
					notifyOver : function(dd, e, data) {
						if (dd.panel.iframeId) {
							var f = Get(dd.panel.iframeId);
							if (f) {
								f.contentWindow.document.write("");
								f.contentWindow.document.close();
							}
						}
						var xy = e.getXY(), portal = this.portal, px = dd.proxy;
						if (!this.grid) {
							this.grid = this.getGrid();
						}
						var cw = portal.body.dom.clientWidth;
						if (!this.lastCW) {
							this.lastCW = cw;
						} else if (this.lastCW != cw) {
							this.lastCW = cw;
							portal.doLayout();
							this.grid = this.getGrid();
						}
						var col = 0, xs = this.grid.columnX, cmatch = false;
						for ( var len = xs.length; col < len; col++) {
							if (xy[0] < (xs[col].x + xs[col].w)) {
								cmatch = true;
								break;
							}
						}
						if (!cmatch) {
							col--;
						}
						var p, match = false, pos = 0, c = portal.items
								.itemAt(col), items = c.items.items, overSelf = false;
						for ( var len = items.length; pos < len; pos++) {
							p = items[pos];
							var h = p.el.getHeight();
							if (h === 0) {
								overSelf = true;
							} else if ((p.el.getY() + (h / 2)) > xy[1]) {
								match = true;
								break;
							}
						}
						pos = (match && p ? pos : c.items.getCount())
								+ (overSelf ? -1 : 0);
						var overEvent = this.createEvent(dd, e, data, col, c,
								pos);
						if (portal.fireEvent('validatedrop', overEvent) !== false
								&& portal
										.fireEvent('beforedragover', overEvent) !== false) {
							px.getProxy().setWidth('auto');
							if (p) {
								px.moveProxy(p.el.dom.parentNode,
										match ? p.el.dom : null);
							} else {
								px.moveProxy(c.el.dom, null);
							}
							this.lastPos = {
								c : c,
								col : col,
								p : overSelf || (match && p) ? pos : false
							};
							this.scrollPos = portal.body.getScroll();
							portal.fireEvent('dragover', overEvent);
							return overEvent.status;
						} else {
							return overEvent.status;
						}
					},
					notifyOut : function() {
						delete this.grid;
					},
					notifyDrop : function(dd, e, data) {
						delete this.grid;
						if (!this.lastPos) {
							return;
						}
						var c = this.lastPos.c, col = this.lastPos.col, pos = this.lastPos.p;
						var dropEvent = this.createEvent(dd, e, data, col, c,
								pos !== false ? pos : c.items.getCount());
						if (this.portal.fireEvent('validatedrop', dropEvent) !== false
								&& this.portal.fireEvent('beforedrop',
										dropEvent) !== false) {
							dd.proxy.getProxy().remove();
							dd.panel.el.dom.parentNode
									.removeChild(dd.panel.el.dom);
							if (pos !== false) {
								if (c == dd.panel.ownerCt
										&& (c.items.items.indexOf(dd.panel) <= pos)) {
									pos++;
								}
								c.insert(pos, dd.panel);
							} else {
								c.add(dd.panel);
							}
							c.doLayout();
							this.portal.fireEvent('drop', dropEvent);
							var st = this.scrollPos.top;
							if (st) {
								var d = this.portal.body.dom;
								setTimeout(function() {
									d.scrollTop = st;
								}, 10);
							}
						}
						delete this.lastPos;
						dd.panel
								.setWidth(dd.panel.el.dom.parentNode.clientWidth
										- parseInt(dd.panel.el.dom.parentNode.style.paddingRight)
										- 3);
						if (dd.panel.iframeId) {
							var f = Get(dd.panel.iframeId);
							if (f && !dd.panel.collapsed) {
								f.style.visibility = "hidden";
								SubmitParams(dd.panel.filename, null,
										dd.panel.iframeId);
								f.style.visibility = "visible";
							}
						}
					},
					getGrid : function() {
						var box = this.portal.bwrap.getBox();
						box.columnX = [];
						this.portal.items.each(function(c) {
							box.columnX.push( {
								x : c.el.getX(),
								w : c.el.getWidth()
							});
						});
						return box;
					},
					unreg : function() {
						Ext.ux.Portal.DropZone.superclass.unreg.call(this);
					}
				});
Ext.ux.PortalColumn = Ext.extend(Ext.Container, {
	layout : 'anchor',
	autoEl : 'div',
	defaultType : 'portlet',
	cls : 'x-portal-column'
});
Ext.reg('portalcolumn', Ext.ux.PortalColumn);
Ext.ux.Portlet = Ext.extend(Ext.Panel, {
	anchor : '100%',
	frame : true,
	collapsible : true,
	draggable : true,
	cls : 'x-portlet',
	beforeDestroy : function() {
		try {
			var o = Get(this.iframeId);
			o.contentWindow.document.write('');
			o.contentWindow.document.close();
			Ext.ux.Portlet.superclass.beforeDestroy.call(this);
		} catch (e) {
		}
	},
	onRender : function(component) {
		Ext.ux.Portlet.superclass.onRender.call(this, component);
		var element = this;
		var portlet_resizer = new Ext.Resizable(this.id, {
			handles : 's',
			pinned : false
		});
		portlet_resizer.on("resize", function(resizer, width, height, event) {
			element.setHeight(height);
			Get(element.iframeId).style.height = height - 28;
		});
		element.on("expand", function() {
			if (element.iframeId) {
				var f = Get(element.iframeId);
				if (f) {
					f.style.visibility = "hidden";
					SubmitParams(element.filename, null, element.iframeId);
					f.style.visibility = "visible";
				}
			}
		});
		element.on("collapse", function() {
			if (element.iframeId) {
				var f = Get(element.iframeId);
				if (f) {
					f.contentWindow.document.write("");
					f.contentWindow.document.close();
				}
			}
		});
		element.on("resize", function(a, b, c, d, e) {
			var f = Get(element.iframeId);
			if (b)
				f.style.width = b - 3;
			if (c)
				f.style.height = c - 28;
		});
		element.header.on("dblclick", function() {
			addTab(element.filename, element.title, element.iconCls);
		});
	}
});
Ext.reg('portlet', Ext.ux.Portlet);
