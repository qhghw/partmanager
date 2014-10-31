Ext.namespace("Ext.ux.huadong");
//LOV基类
Ext.ux.huadong.GridTriggerField = Ext.extend(Ext.form.TriggerField, {

	triggerClass : 'x-form-ref-trigger',
	defaultAutoCreate : {
		tag : "input",
		type : "text",
		size : "10",
		autocomplete : "off"
	}, // *
	fieldName : '',
	queryFields : [],
	queryFieldsMap : null,
	paramMapping:[],
	defQueryField : '',
	dataComponentName : null,
	initComponent : function() {
		Ext.ux.huadong.GridTriggerField.superclass.initComponent.call(this);
		this.addEvents('select');
		this.queryFieldsMap = [new Ext.form.ComboBox({
							triggerAction:'all',
							value : this.defQueryField,
							selectOnFocus : true,
							name : this.dataComponentName + '_QRY_FIELD',
							hiddenName : this.dataComponentName
									+ '_QRY_FIELD_ID',
							hiddenId : this.dataComponentName + '_QRY_FIELD_ID',
							width : 120,
							store : this.queryFields
/* getQueryFieldLabels() */}), new Ext.form.TextField({
							xtype : 'textfield',
							enableKeyEvents:true,
							name : this.dataComponentName + '_QRY_VALUE',
							width : 150,
							listeners : {
								'keyup' : function(field, e) {
									field.setValue(field.getValue().toUpperCase()); 
								}
							}
							
						})];
		// alert(this.queryFieldsMap[0]);
		

		this.pagingBar = new Ext.PagingToolbar({
					pageSize : 10,
					store : this.store,
					displayInfo : true,
					displayMsg : '第 {0} - {1} 条  共 {2} 条',
				    emptyMsg : "没有记录"
				});

		this.gridMain = new Ext.grid.GridPanel({
					store : this.store,
					cm : this.cm,
					sm : new Ext.grid.RowSelectionModel(),
					autoSize : true,
					loadMask : true,
					bbar : this.pagingBar

				});

	},
	initEvents : function() {
		Ext.ux.huadong.GridTriggerField.superclass.initEvents.call(this);
		this.on('specialkey', this.onSpecialKeyClick, this);
		this.gridMain.on('keypress', this.onViewKeypress, this);
		this.gridMain.on('dblclick', this.onDoSelect, this);
		
	},
	onRender : function(ct, position) {
		Ext.ux.huadong.GridTriggerField.superclass.onRender.call(this, ct,
				position);
		// this.on('focus', this.initList, this, { single: true });
	},
	onTriggerClick : function() {
		this.showLov();
	},
	showLov : function() {
		this.prompting = true;

		if (this.autoLoad) {
			this.executeQuery();
		}

		if (!this.windowRef) {
			this.windowRef = new Ext.Window({
						buttonAlign : 'center',
						layout : 'fit',
						modal : true,
						height : this.gridHeight,
						width : this.gridWidth,
						title : this.gridTitle,
						closeAction : 'hide',
						plain : true,
						items : [this.gridMain],
						tbar : [{
									xtype : 'label',
									text : '过滤条件:'
								}, this.queryFieldsMap[0],
								this.queryFieldsMap[1], {
									xtype : 'button',
									text : '确定',
									handler : this.executeQuery,
									scope : this
								},
								 {
									xtype : 'button',
									text : '清除',
									handler : this.clearQuery,
									scope : this
								}],
					   buttons:
                        [
                           {text:'选择', handler:this.onDoSelect, scope:this, xtype:"button"}
                          ,{text:'取消', handler:this.onDoCancel, scope:this, xtype:"button"}
                       
                        ]
					});
		}

		this.windowRef.show();
		this.windowRef.anchorTo(this.getItemId(), 'tl', [0, 20]);
	},
	executeQuery : function() {
		var p = new Object();
		p['_p_query_column'] = this.queryFieldsMap[0].getValue();
		p['_p_query_value'] = this.queryFieldsMap[1].getValue();
		p['_p_query_fieldName'] = this.fieldName;
		this.store.baseParams = p;
		this.store.load();
	},
	clearQuery : function() {
		var p = new Object();
		this.queryFieldsMap[0].setValue("");
		this.queryFieldsMap[1].setValue("");
		p['_p_query_column'] = "";
		p['_p_query_value'] = "";
		this.store.baseParams = p;
		this.store.load();
	},
	setValue : function(text) {
		Ext.ux.huadong.GridTriggerField.superclass.setValue.call(this, text);
	},
	validateBlur : function() {
		return !this.prompting;
	},
	getSelectedRecord : function() {
		return this.gridMain.getSelectionModel().getSelected();
	},
	onViewKeypress : function(evnt) { // alert(evnt.getKey());
		if (evnt.getKey() == 13) { // arrow down => open lov
			this.onDoSelect();
		}
	},
	onSpecialKeyClick : function(field, evnt) {
		if (evnt.getKey() == Ext.EventObject.DOWN) { // arrow down =>
			// open lov
			this.showLov();
		}
		return false;
	},
	onDoSelect : function() {
		var selectedItems = this.gridMain.selModel.selections.keys;
		if (selectedItems.length == 0) {
			Ext.MessageBox.alert('错误', '请选择数据!');
		} else {

			var selectedRow = this.gridMain.getSelectionModel().getSelected();
			var text = selectedRow.get(this.gridIndex);
			
			this.setValue(text);
			this.fireEvent('select', this, selectedRow);
			this.prompting = false;
			this.windowRef.hide();
			
		}
	},
	onDoCancel: function() {
       // this.select(this.selections);
        this.windowRef.hide();
        this.focus();
    }
});

//自定义形成的指标弹出窗口
Ext.ux.huadong.MeaningComboBox=Ext.extend(Ext.ux.huadong.GridTriggerField, {
			dataComponentName : "meaningcombobox",
			queryFields : [["code", "体系代码"], ["meaning", "体系名称"]],
		//	name : 'namapelayanan',
		//	fieldLabel : 'Nama Pelayanan',
			readOnly:true,
			triggerClass : 'x-form-search-trigger',
			enableKeyEvents : true,
			gridTitle : '选择',
			gridIndex : 'code',
			gridHeight : 360,
			gridWidth : 400,
			autoLoad : true,
			fieldName : '',
			store : new Ext.data.JsonStore({
						totalProperty : "totalProperty",
						root : "root",
						url : "getMeaningList.action",
						remoteSort : true,
						fields : [{
									name : "code",
									type : "string"
								},{
									name : "meaning",
									type : "string"
								}]

					}),
			cm : new Ext.grid.ColumnModel([
					 
			         {
						id : "code",
						header : "体系代码",
						width : 120,
						dataIndex : 'code',
						sortable : true
					}, {
						id : "meaning",
						header : "体系名称",
						width : 200,
						dataIndex : 'meaning',
						sortable : true
					}]),
			selectOnFocus : true,
			width : 160
		});

Ext.reg('meaningcombobox',Ext.ux.huadong.MeaningComboBox);