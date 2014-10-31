function extPopulateFilter(win, grid) {
	var store, eStore, vStore, jStore;
	if (win.conIndex)
		win.conIndex = win.conIndex - 1;
	else
		win.conIndex = 9999;
	if (win.filterStore) {
		store = win.filterStore;
		eStore = win.eStore;
		vStore = win.vStore;
		jStore = win.jStore;
	} else {
		store = new Ext.data.Store( {
			proxy : new Ext.data.PagingMemoryProxy( []),
			reader : new Ext.data.ArrayReader( {}, [ {
				name : "findField",
				type : "string"
			}, {
				name : "findLabel",
				type : "string"
			} ])
		});
		var meta = Ext.data.Record.create(store.fields.items);
		var col = grid.getColumnModel().config;
		var rec, i, j = col.length;
		rec = new meta( {
			findField : "",
			findLabel : "[取消该条件]"
		});
		store.add(rec);
		rec.commit();
		for (i = 0; i < j; i++) {
			var data = {}, rec;
			if (!IsEmpty(col[i]["dataIndex"]) && !IsEmpty(col[i]["header"])) {
				data["findField"] = col[i]["dataIndex"];
				data["findLabel"] = col[i]["header"].replace(/<[^>].*?>/g,"");
				rec = new meta(data);
				store.add(rec);
				rec.commit();
			}
		}
		win.filterStore = store;
		eStore = new Ext.data.Store( {
			proxy : new Ext.data.PagingMemoryProxy( []),
			reader : new Ext.data.ArrayReader( {}, [ {
				name : "findField",
				type : "string"
			}, {
				name : "findLabel",
				type : "string"
			} ])
		});
		meta = Ext.data.Record.create(store.fields.items);
		col = [ [ "=", "等于" ], [ "<>", "不等于" ], [ ">", "大于" ], [ "<", "小于" ],
				[ ">=", "大于等于" ], [ "<=", "小于等于" ], [ "is null", "为空" ],
				[ "is not null", "不为空" ], [ "like", "like" ],
				[ "not like", "not like" ], [ "in", "in" ],
				[ "not in", "not in" ] ];
		j = col.length;
		for (i = 0; i < j; i++) {
			var data = {}, rec;
			data["findField"] = col[i][0];
			data["findLabel"] = col[i][1];
			rec = new meta(data);
			eStore.add(rec);
			rec.commit();
		}
		win.eStore = eStore;
		vStore = new Ext.data.Store( {
			proxy : new Ext.data.PagingMemoryProxy( []),
			reader : new Ext.data.ArrayReader( {}, [ {
				name : "findField",
				type : "string"
			}, {
				name : "findLabel",
				type : "string"
			} ])
		});
		win.vStore = vStore;
		jStore = new Ext.data.Store( {
			proxy : new Ext.data.PagingMemoryProxy( []),
			reader : new Ext.data.ArrayReader( {}, [ {
				name : "findField",
				type : "string"
			}, {
				name : "findLabel",
				type : "string"
			} ])
		});
		meta = Ext.data.Record.create(store.fields.items);
		col = [ [ "and", "并且" ], [ "or", "或者" ] ];
		j = col.length;
		for (i = 0; i < j; i++) {
			var data = {}, rec;
			data["findField"] = col[i][0];
			data["findLabel"] = col[i][1];
			rec = new meta(data);
			jStore.add(rec);
			rec.commit();
		}
		win.jStore = jStore;
	}
	if (win.currentTop)
		win.currentTop = win.currentTop + 33;
	else
		win.currentTop = 10;
	var c = new Ext.form.ComboBox(
			{
				id : "cb__" + win.conIndex,
				name : "cb__" + win.conIndex,
				valCombo : "vq__" + win.conIndex,
				x : 15,
				y : win.currentTop,
				store : store,
				mustSelection : true,
				valueField : "findField",
				displayField : "findLabel",
				emptyText : "选择列",
				editable : false,
				mode : "local",
				triggerAction : "all",
				listeners : {
					select : function(control) {
						var newValue = control.getValue();
						var b = Ext.getCmp(control.valCombo), s = b.store, x = extFindMapper(
								grid, newValue), t;
						if (s.lastField && s.lastField == newValue)
							return;
						b.comboObject = control;
						s.lastField = newValue;
						try {
							t = eval("typeof(" + x
									+ "__kdt)=='undefined'?null:" + x
									+ "__kdt;");
						} catch (exp) {
							t = false;
						}
						s.removeAll();
						b.mustSelection = t;
						if (t) {
							meta = Ext.data.Record.create(s.fields.items);
							j = t.length;
							for (i = 0; i < j; i++) {
								var data = {}, rec;
								data["findField"] = t[i][1];
								data["findLabel"] = t[i][0];
								rec = new meta(data);
								s.add(rec);
								rec.commit();
							}
						}
						extPopulateDatePicker(b, grid, newValue);
					}
				}
			});
	win.add(c);
	c = new Ext.form.ComboBox( {
		id : "eq__" + win.conIndex,
		name : "eq__" + win.conIndex,
		x : 195,
		y : win.currentTop,
		width : 80,
		store : eStore,
		valueField : "findField",
		displayField : "findLabel",
		value : "=",
		mustSelection : true,
		emptyText : "选择比较",
		editable : false,
		mode : "local",
		triggerAction : "all"
	});
	win.add(c);
	c = new Ext.form.ComboBox( {
		id : "vq__" + win.conIndex,
		name : "vq__" + win.conIndex,
		x : 290,
		y : win.currentTop,
		store : vStore,
		valueField : "findField",
		displayField : "findLabel",
		emptyText : "输入值",
		mode : "local",
		triggerAction : "all",
		menuListeners : {
			select : function(m, d) {
				this.setValue(extFormatDate(d));
				this.fireEvent('select', this, d);
			},
			show : function() {
				this.onFocus();
			},
			hide : function() {
				this.focus.defer(10, this);
				var ml = this.menuListeners;
				this.menu.un("select", ml.select, this);
				this.menu.un("show", ml.show, this);
				this.menu.un("hide", ml.hide, this);
			}
		},
		onTriggerClick : function() {
			if (this.allowPopDate) {
				if (this.menu == null) {
					this.menu = new Ext.menu.DateMenu();
				}
				this.menu.on(Ext.apply( {}, this.menuListeners, {
					scope : this
				}));
				var y = this.el.dom.value, d;
				if (IsEmpty(y) || y == this.emptyText)
					d = new Date();
				else {
					d = Date.parseDate(y, "Y-n-j");
					if (IsEmpty(d))
						d = new Date();
				}
				this.menu.picker.setValue(d);
				this.menu.show(this.el, "tl-bl?");
			} else
				Ext.form.ComboBox.prototype.onTriggerClick.call(this);
		},
		listeners : {
			focus : function(control) {
				if (control.comboObject)
					control.comboObject
							.fireEvent("select", control.comboObject);
			}
		}
	});
	win.add(c);
	var c = new Ext.form.ComboBox( {
		id : "jb__" + win.conIndex,
		name : "jb__" + win.conIndex,
		x : 470,
		y : win.currentTop,
		width : 70,
		store : jStore,
		valueField : "findField",
		displayField : "findLabel",
		emptyText : "更多...",
		canAddcombo : true,
		mustSelection : true,
		editable : false,
		mode : "local",
		triggerAction : "all",
		listeners : {
			select : function(control, record, index) {
				if (control.canAddcombo) {
					extPopulateFilter(win, grid);
					control.canAddcombo = false;
				}
			}
		}
	});
	win.add(c);
	win.doLayout();
}

function extPopulateDatePicker(combo, grid, field) {
	var items = grid.store.fields.items;
	var i, j;

	j = items.length;
	for (i = 0; i < j; i++) {
		if (items[i]["name"] == field && items[i]["type"] == "date") {
			combo.allowPopDate = true;
			return;
		}
	}
	combo.allowPopDate = false;
}

function extFindMapper(grid, field) {
	var col = grid.getColumnModel().config;
	var i, j = col.length;
	for (i = 0; i < j; i++) {
		if (!IsEmpty(col[i]["dataIndex"]) && !IsEmpty(col[i]["header"])
				&& col[i]["dataIndex"] == field) {
			if (IsEmpty(col[i]["mapKey"]))
				return field;
			else
				return col[i]["mapKey"]
		}
	}
}