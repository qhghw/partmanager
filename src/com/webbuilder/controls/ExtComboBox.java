package com.webbuilder.controls;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.servlet.http.HttpServletRequest;

public class ExtComboBox extends ExtTextField {
	public String store = "";
	public String data = "";
	public String query = "";
	public String fields = "";
	public String displayField = "";
	public String valueField = "";
	public String hintExpress = "";
	public String forceSelection = "";
	public String typeAhead = "";
	public String selectOnFocus = "";
	public String mode = "";
	public String listWidth = "";
	public String resizable = "";
	public String loadStore = "";
	public String editable = "";
	public String onSelect = "";
	public String onBeforeQuery = "";
	public String onExpand = "";
	public String onCollapse = "";
	public String onBeforeSelect = "";
	public String hideTrigger = "";
	public String loadingText = "";
	public String minChars = "";
	public String pageSize = "";
	public String mustSelection = "";
	public String mapKey = "";
	public boolean createHidden = false;
	public boolean dateAsString = true;
	protected boolean emptyItems = false;

	protected void addChildProperty() throws Exception {
		String storeName = null;
//如果key 非空与只允许输入条目数据
		if ((!StringUtil.isEmpty(this.mapKey))
				&& (StringUtil.isEmpty(this.mustSelection)))
			this.mustSelection = "true";
		if (StringUtil.getStringBool(this.mustSelection)) {
			setExpress("mustSelection", "true");
			this.validator = ("{var v=extGetComboValue(" + this.name
					+ ");if(v==null)return false;}" + this.validator + "return true;");
			if (StringUtil.isEmpty(this.invalidText))
				this.invalidText = "请选择列表中的值。";
		}
		super.addChildProperty();
		if (!this.emptyItems) {
			//如果store非空
			if (!StringUtil.isEmpty(this.store)) {
				storeName = this.store;
				if (!StringUtil.isEmpty(this.mapKey)){
					if(this.mapKey.split("@").length>0)
					{
						String arr[]=this.mapKey.split("@");
						String arrayName = this.mapKey+ "__kdt";
						DbUtil.createKeyQuery_adv(this.request, arr[0]);
						setHeaderScript((String) this.request.getAttribute("keya." + arr[0]));
						/*if (DbUtil.createKeyQuery(this.request, arr[0]))
							setHeaderScript((String) this.request.getAttribute("keya." + arr[0]));
						setFirstExtScript(storeName
								+ "=new Ext.data.Store({proxy:new Ext.data.PagingMemoryProxy("
								+ arrayName + "),reader:new Ext.data.ArrayReader({},["
								+ this.fields + "])});");*/
					}
				}
				
			} else {
				ResultSet resultSet = fetchResultSet(this.query);
				ResultSetMetaData meta = null;
				String arrayName = this.name + "__data";
				storeName = this.name + "__store";
				setHeaderScript("var " + storeName + ";");
				if (!StringUtil.isEmpty(this.mapKey)) {
					arrayName = this.mapKey + "__kdt";
					if (DbUtil.createKeyQuery(this.request, this.mapKey))
						setHeaderScript((String) this.request
								.getAttribute("keya." + this.mapKey));
					if (StringUtil.isEmpty(this.displayField))
						this.displayField = "KEY_TEXT";
					if (StringUtil.isEmpty(this.valueField))
						this.valueField = "KEY_ID";
					if (StringUtil.isEmpty(this.fields))
						this.fields = "'KEY_TEXT',{name:'KEY_ID',type:'string'}";
				} else if (!StringUtil.isEmpty(this.data)) {
					setHeaderScript("var " + this.name + "__data=[" + this.data
							+ "];");
				} else if (resultSet != null) {
					meta = resultSet.getMetaData();
					if (StringUtil.isEmpty(this.displayField))
						this.displayField = meta.getColumnLabel(1);
					if (StringUtil.isEmpty(this.valueField))
						if (meta.getColumnCount() > 1)
							this.valueField = meta.getColumnLabel(2);
						else
							this.valueField = this.displayField;
					setHeaderScript("var " + this.name + "__data="
							+ DbUtil.getDataArray(resultSet, meta, false) + ";");
					if (StringUtil.isEmpty(this.fields))
						this.fields = DbUtil.getFieldsJson(meta,
								this.dateAsString);
				} else {
					setHeaderScript("var " + this.name + "__data=[];");
				}
				setFirstExtScript(storeName
						+ "=new Ext.data.Store({proxy:new Ext.data.PagingMemoryProxy("
						+ arrayName + "),reader:new Ext.data.ArrayReader({},["
						+ this.fields + "])});");
			}
			setExpress("store", storeName);
		}
		setExpressString("displayField", this.displayField);
		if (!StringUtil.isEqual(this.valueField, "-"))
			setExpressString("valueField", this.valueField);
		if ((!StringUtil.isEmpty(this.hintExpress))
				&& (!StringUtil.isEmpty(this.displayField)))
			setExpressString("tpl", "<tpl for=\\\".\\\"><div ext:qtip=\\\""
					+ this.hintExpress
					+ "\\\" class=\\\"x-combo-list-item\\\">{"
					+ this.displayField + "}</div></tpl>");
		setExpress("forceSelection", this.forceSelection);
		setExpress("typeAhead", this.typeAhead);
		setExpress("hideTrigger", this.hideTrigger);
		setExpress("listWidth", this.listWidth);
		setExpress("resizable", this.resizable);
		setExpress("editable", this.editable);
		if (this.createHidden)
			setExpressString("hiddenName", this.name + "__hv");
		setExpress("selectOnFocus", this.selectOnFocus);
		setExpress("minChars", this.minChars);
		setExpress("pageSize", this.pageSize);
		if (!StringUtil.isEmpty(this.pageSize)) {
			this.mode = "remote";
			this.loadStore = "false";
		}
		setExpressString("mode", this.mode);
		if (StringUtil.isEqual(this.loadingText, "-"))
			setExpress("loadingText", "\"\"");
		else
			setExpressString("loadingText", this.loadingText);
		setExpressString("triggerAction", "all");
		setEventItem("select", this.onSelect, "control,record,index");
		setEventItem("beforeselect", this.onBeforeSelect,
				"control,record,index");
		setEventItem("beforequery", this.onBeforeQuery, "event");
		setEventItem("expand", this.onExpand, "control");
		setEventItem("collapse", this.onCollapse, "control");
		if ((!this.emptyItems)
				&& ((StringUtil.isEmpty(this.loadStore)) || (StringUtil
						.getStringBool(this.loadStore))))
			setLastExtScript(storeName + ".load();");
	}
	protected String getObjectClass() {
		return "Ext.form.ComboBox";
	}

	protected String getObjectXType() {
		return "combo";
	}
}
