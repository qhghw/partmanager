function Get(id) {
	return document.getElementById(id);
}

function extractFileName(f) {
	if (IsEmpty(f))
		return "";
	var p1 = f.lastIndexOf("\\"), p2 = f.lastIndexOf("/");
	if (p1 < p2)
		p1 = p2;
	if (p1 == -1)
		return f;
	else
		return f.substring(p1 + 1);
}

function ExtractTitle(t) {
	if (IsEmpty(t))
		return "";
	else
		return t.replace(/<\/?.+?>/g, "").trim();
}

function GetShortTitle(title) {
	if (title.length > 13)
		return '<span title="' + Ext.util.Format.htmlEncode(title) + '">'
				+ Ext.util.Format.htmlEncode(title.substring(0, 10))
				+ '...</span>';
	else
		return title;
}

function SubmitToServer(url, params, title, iconCls) {
	var p = window, t = window.top;
	while (p.parent && !p.appendModule) {
		p = p.parent;
		if (p == t)
			break;
	}
	if (p.appendModule && !IsEmpty(title)) {
		var ic = iconCls;
		if (IsEmpty(ic))
			ic = "icon_item";
		p.appendModule(url, title, ic, params);
	} else
		SubmitParams(url, params);
}

function SubmitParams(url, params, target) {
	var el, fm = Get("wb_gsubform");
	if (fm) {
		while (fm.childNodes.length !== 0) {
			fm.removeChild(fm.childNodes[0]);
		}
	} else {
		fm = document.createElement("FORM");
		fm.setAttribute("name", "wb_gsubform");
		fm.setAttribute("id", "wb_gsubform");
		fm.method = "POST";
		document.body.appendChild(fm);
	}
	if (params) {
		for (n in params) {
			el = document.createElement("input");
			el.setAttribute("name", n);
			el.setAttribute("type", "hidden");
			if (Ext.isDate(params[n]))
				el.setAttribute("value", extFormatDate(params[n], true));
			else
				el.setAttribute("value", params[n]);
			fm.appendChild(el);
		}
	}
	fm.action = url;
	if (!target)
		target = "_blank";
	fm.target = target;
	fm.submit();
}

function duplicateString(s, n) {
	var i, r = new Array();

	for (i = 0; i < n; i++)
		r.push(s);
	return r.join("");
}

function MoveOptions(SrcName, DstName, IsAll) {
	var Src = Get(SrcName);
	var Dst = Get(DstName);
	var I = 0, SrcLength = Src.length, SrcIndex = 0;

	if (IsAll)
		for (I = 0; I < SrcLength; I++)
			Src.options[I].selected = true;
	SrcIndex = Src.selectedIndex;
	if (SrcIndex == -1) {
		if (Src.length > 0)
			Src.options[0].selected = true;
	} else {
		var K = 0, DstLength = Dst.length, DstIndex = DstLength - 1, SelCount = 0;

		for (I = DstLength - 1; I >= 0; I--)
			if (Dst.options[I].selected) {
				DstIndex = I;
				break;
			}
		for (I = 0; I < SrcLength; I++)
			if (Src.options[I].selected)
				SelCount++;
		for (I = DstLength - 1; I > DstIndex; I--)
			Dst.options[I + SelCount] = new Option(Dst.options[I].text,
					Dst.options[I].value);
		for (I = SrcLength - 1; I >= 0; I--)
			if (Src.options[I].selected) {
				Dst.options[DstIndex + SelCount - K] = new Option(
						Src.options[I].text, Src.options[I].value);
				Src.options[I] = null;
				K++;
			}
		if (SrcIndex < Src.length)
			Src.options[SrcIndex].selected = true;
		else if (Src.length > 0)
			Src.options[Src.length - 1].selected = true;
		DstLength = Dst.length;
		for (I = 0; I < DstLength; I++)
			Dst.options[I].selected = I > DstIndex && I <= DstIndex + SelCount;
	}
}

function GetDateTime(dt) {
	var d = dt.getValue();
	if (dt.timeControl == null)
		return d;
	var t = Ext.getCmp(dt.timeControl).getValue();
	if (IsEmpty(t))
		t = "0:0:0";
	if (IsEmpty(d))
		return "";
	else {
		var tm = t.split(":");
		return new Date(d.getFullYear(), d.getMonth(), d.getDate(), tm[0],
				tm[1], tm[2]);
	}
}

function AddToList(edit, val, text) {
	if (IsEmpty(val))
		return;
	var s = edit.getValue().trim();
	if (s.indexOf(val) != -1)
		return;
	if (!IsEmpty(s)) {
		if (s.substring(s.length - 1) != ",")
			s += ",";
		s += "\r\n";
	}
	if (IsEmpty(text))
		text = "";
	edit.setValue(s + val + text);
}

function AddToUserList(edit, user, text) {
	if (extInvalidUser(user)) {
		extShowWarning('请选择一个有效的人员。', function() {
			user.focus(true, true);
		});
		return;
	}
	AddToList(edit, user.getValue(), text);
}

function ClearOptions(ListName) {
	ClearListOptions(Get(ListName));
}

function ClearListOptions(List) {
	var I, J;

	J = List.length;
	for (I = J - 1; I >= 0; I--)
		List.options[I] = null;
}

function GetIndexFromList(List, Text) {
	var I, J;

	J = List.length;
	for (I = 0; I < J; I++)
		if (List.options[I].text == Text)
			return I;
	return -1;
}

function AddOptionsFromArray(SrcListName, DstListName, TextArray, DataArray,
		BeginIndex, EndIndex, Sorted) {
	var I, J, K, hasData;
	var SrcArray = new Array();
	var SrcList = Get(SrcListName);
	var DstList = Get(DstListName);
	var notHasDst = DstListName == null;

	J = TextArray.length;
	hasData = DataArray != null;
	if (EndIndex == -1)
		EndIndex = J - 1;
	J = EndIndex - BeginIndex + 1;
	K = 0;
	for (I = 0; I < J; I++)
		if (notHasDst
				|| GetIndexFromList(DstList, TextArray[BeginIndex + I]) == -1) {
			SrcArray[K] = new Array(2);
			SrcArray[K][0] = TextArray[BeginIndex + I];
			if (hasData)
				SrcArray[K][1] = DataArray[BeginIndex + I];
			else
				SrcArray[K][1] = TextArray[BeginIndex + I];
			K++;
		}
	if (Sorted)
		SrcArray.sort();
	J = SrcArray.length;
	for (I = 0; I < J; I++)
		SrcList.options[I] = new Option(SrcArray[I][0], SrcArray[I][1]);
}

function AddSimpleOptions(ListName, TextArray, DataArray, Sorted) {
	AddOptionsFromArray(ListName, null, TextArray, DataArray, 0, -1, Sorted);
}

function StringInJsonArray(string, json) {
	var i, j = json.length;

	for (i = 0; i < j; i++)
		if (json[i] == string)
			return true;
	return false;
}

function IsNewJsonArray(source, dest) {
	var name;

	if (dest == null)
		return false;
	for (name in source)
		if (dest[name] == null && !IsEmpty(source[name]) || dest[name] != null
				&& !IsEmpty(dest[name]))
			return false;
	return true;
}

function BoolToInt(val) {
	if (val)
		return 1;
	else
		return 0;
}

function GetObjectFromArray(dataArray, keyName, keyValue) {
	var i, j;

	j = dataArray.length;
	for (i = 0; i < j; i++)
		if (dataArray[i][keyName] == keyValue)
			return dataArray[i];
	return null;
}

function FormatNumber(num, pattern) {
	if (IsEmpty(num))
		return '';
	var strarr = num ? num.toString().split('.') : [ '0' ];
	var fmtarr = pattern ? pattern.split('.') : [ '' ];
	var retstr = '', floatVal, floatFmt, roundVal;

	floatVal = strarr.length > 1 ? strarr[1] : '';
	if (!IsEmpty(floatVal)) {
		floatFmt = fmtarr.length > 1 ? fmtarr[1] : '';
		if (!IsEmpty(floatFmt)) {
			var pr = Math.pow(10, floatFmt.length);
			roundVal = Math.round(num * pr) / pr;
		} else
			roundVal = Math.round(num);
	} else
		roundVal = num;
	var strarr = roundVal ? roundVal.toString().split('.') : [ '0' ];
	var str = strarr[0];
	var fmt = fmtarr[0];
	var i = str.length - 1;
	var comma = false;
	for ( var f = fmt.length - 1; f >= 0; f--) {
		switch (fmt.substr(f, 1)) {
		case '#':
			if (i >= 0)
				retstr = str.substr(i--, 1) + retstr;
			break;
		case '0':
			if (i >= 0)
				retstr = str.substr(i--, 1) + retstr;
			else
				retstr = '0' + retstr;
			break;
		case ',':
			comma = true;
			retstr = ',' + retstr;
			break;
		}
	}
	if (i >= 0) {
		if (comma) {
			var l = str.length;
			for (; i >= 0; i--) {
				retstr = str.substr(i, 1) + retstr;
				if (i > 0 && ((l - i) % 3) == 0)
					retstr = ',' + retstr;
			}
		} else
			retstr = str.substr(0, i + 1) + retstr;
	}
	retstr = retstr + '.';
	str = strarr.length > 1 ? strarr[1] : '';
	fmt = fmtarr.length > 1 ? fmtarr[1] : '';
	i = 0;
	for ( var f = 0; f < fmt.length; f++) {
		switch (fmt.substr(f, 1)) {
		case '#':
			if (i < str.length)
				retstr += str.substr(i++, 1);
			break;
		case '0':
			if (i < str.length)
				retstr += str.substr(i++, 1);
			else
				retstr += '0';
			break;
		}
	}
	
	return retstr.replace(/^,+/, '').replace(/\.$/, '');
}

function IsEmpty(val) {
	return val == undefined || val == null || val == '' && val != '0';
}

function GetArrayValue(arr, key) {
	var i, j = arr.length;

	for (i = 0; i < j; i++)
		if (arr[i][1] == key)
			return arr[i][0];
	return null;
}

function ResetFileUpload(id) {
	var up = (typeof id == "string") ? Get(id) : id;
	if (typeof up != "object")
		return null;
	var tt = document.createElement("span");
	tt.id = "__tt__";
	up.parentNode.insertBefore(tt, up);
	var tf = document.createElement("form");
	tf.appendChild(up);
	document.getElementsByTagName("body")[0].appendChild(tf);
	tf.reset();
	tt.parentNode.insertBefore(up, tt);
	tt.parentNode.removeChild(tt);
	tt = null;
	tf.parentNode.removeChild(tf);
}

function GetRandomId() {
	return new Date().getTime();
}

function BrowseDocument(w, html, title) {
	w
			.write('<html><head><meta http-equiv="content-type" content="text/html;charset=utf-8"><link rel="stylesheet" href="webbuilder/css/wbstyle.css" type="text/css"></head><body class="sys_normal">');
	w.write(html);
	w.write('</body></html>');
	w.title = title;
	w.close();
}

function BrowseHtml(html, title) {
	SubmitToServer("main?action=webbuilder/application/service/createHtml.xwl",
			{
				script : html,
				caption : title
			}, title, "icon_preview")
}

function TextInArrayPos(list, text) {
	var i, j = list.length;
	for (i = 0; i < j; i++)
		if (list[i] == text)
			return i;
	return -1;
}

function GetLabel(txt, index) {
	var i = txt.indexOf('('), j = txt.indexOf(')');
	if (i == -1 || j == -1)
		return txt;
	else {
		if (index == -1)
			return txt.substring(0, i);
		var s = txt.substring(i + 1, j);
		var r = s.split(",");
		return r[index];
	}
}

function GetHintUser(txt) {
	var i = txt.indexOf('(');
	if (i == -1)
		return txt;
	else
		return txt.substring(0, i);
}

function GetHintCode(txt) {
	var i = txt.indexOf('('), j = txt.indexOf(')');
	if (i == -1 || j == -1)
		return txt;
	else
		return txt.substring(i + 1, j);
}

function extGetNodeEndAttribute(node) {
	var end;

	if (!node.isLeaf())
		return node.attributes.end;
	else if (node.nextSibling != null)
		return node.nextSibling.attributes.begin - 1;
	else if (node.parentNode != null && node.parentNode.attributes.end != null)
		return node.parentNode.attributes.end;
	else
		return -1;
}

function extGetPageGridData(grid) {
	var data = new Array();
	var store = grid.getStore();
	var i, j = store.getCount();

	for (i = 0; i < j; i++)
		data.push(store.getAt(i).data);
	return Ext.encode(data);
}

function extSetDeleteRecords(store, record) {
	if (!IsNewJsonArray(record.data, record.modified)) {
		var data, name, recs = record.data, midif = record.modified;
		if (!IsEmpty(store.deleteRecords))
			data = Ext.decode(store.deleteRecords);
		else
			data = new Array();
		for (name in midif)
			recs[name] = midif[name];
		record.data.__delete = 1;
		data.push(record.data);
		if (store.modified.indexOf(record) != -1)
			store.modified.remove(record);
		store.deleteRecords = Ext.encode(data);
	}
}

function extFindFromNode(node, text) {
	var result = null, child;
	if (node.text == text)
		return node;
	node.eachChild(function(n) {
		child = extFindFromNode(n, text);
		if (child != null) {
			result = child;
			return;
		}
	});
	return result;
}

function extFindTreeNode(tree, text) {
	return extFindFromNode(tree.getRootNode(), text);
}

function  extGetGridJson(grid)
{
	var data = new Array();
	var store = grid.getStore();
	var i, j = store.getCount();

	for (i = 0; i < j; i++)
		data.push(store.getAt(i).data);

	return Ext.encode(data);
}


function extGetGridRecords(grid) {
	var data = new Array();
	var mode = grid.submitMode;

	if (mode == null || mode == "select") {
		if (grid.getXType() == "editorgrid") {
			var record = extGetGridSelRecord(grid);
			if (record != null)
				data.push(record.data);
		} else {
			var rows = grid.getSelectionModel().getSelections();
			var i, j = rows.length;

			for (i = 0; i < j; i++)
				data.push(rows[i].data);
		}
	} else if (mode == "modified") {
		Ext.each(grid.getStore().getModifiedRecords(), function(record) {
			if (IsNewJsonArray(record.data, record.modified))
				data.push(record.data);
			else {
				var name, recs, modif, obj = {};

				recs = record.data;
				modif = record.modified;
				if (modif != null) {
					for (name in recs) {
						obj[name] = recs[name];
						if (modif[name] != null)
							obj[name + "__old"] = modif[name];
						else
							obj[name + "__old"] = recs[name];
					}
					obj["__update"] = 1;
					data.push(obj);
				}
			}
		});
		if (!IsEmpty(grid.getStore().deleteRecords)) {
			var recs = Ext.decode(grid.getStore().deleteRecords);
			var i, j = recs.length;
			for (i = 0; i < j; i++)
				data.push(recs[i]);
		}
	} else {
		var store = grid.getStore();
		var i, j = store.getCount();

		for (i = 0; i < j; i++)
			data.push(store.getAt(i).data);
	}
	return Ext.encode(data);
}

function extGetAllGridData(grid, handler) {
	var store = grid.getStore();
	var newReader, oldReader = store.reader;

	if (oldReader.jsonData != null)
		newReader = new Ext.data.JsonReader(oldReader.meta, store.fields.items);
	else if (oldReader.xmlData != null)
		newReader = new Ext.data.XmlReader(oldReader.meta, store.fields.items);
	else
		newReader = new Ext.data.ArrayReader( {}, store.fields.items);
	var newStore = new Ext.data.Store( {
		proxy : store.proxy,
		reader : newReader,
		baseParams : store.baseParams,
		listeners : {
			load : function(control, records, options) {
				var i, j = control.getCount();
				var data = new Array();
				for (i = 0; i < j; i++)
					data.push(control.getAt(i).data);
				handler(Ext.encode(data));
			}
		}
	});
	newStore.load({params:{start:0,limit:30000000}});
}

function extGetAllControlsValueArray(control, dataArray) {
	var xtype, namePart, valuePart, subControl;

	namePart = control.id;
	if (namePart != null) {
		if (control.getXType != null)
			xtype = control.getXType();
		else
			xtype = null;
		if (xtype != null) {
			valuePart = null;
			if (xtype == "textfield" || xtype == "hidden"
					|| xtype == "textarea" || xtype == "timefield"
					|| xtype == "htmleditor" || xtype == "slider")
				valuePart = control.getValue();
			else if (xtype == "checkbox" && control.simpleCheck != null)
				valuePart = control.getValue() ? 1 : 0;
			else if (xtype == "combo") {
				if (control.el) {
					valuePart = control.el.dom.value;
					if (valuePart == control.emptyText)
						valuePart = "";
				} else
					valuePart = "";
				if (!IsEmpty(valuePart)
						&& (control.mustSelection || control.forceSelection || !control.editable))
					valuePart = control.getValue();
			} else if (xtype == "datefield") {
				if (control.timeControl == null)
					valuePart = control.getValue();
				else {
					var dt = control.getValue();
					var t = Ext.getCmp(control.timeControl).getValue();

					if (IsEmpty(t))
						t = "0:0:0";
					if (IsEmpty(dt))
						valuePart = "";
					else {
						var tm = t.split(":");
						valuePart = new Date(dt.getFullYear(), dt.getMonth(),
								dt.getDate(), tm[0], tm[1], tm[2]);
					}
				}

			} else if (xtype == "numberfield")
				valuePart = control.getValue();
			else if (xtype == "checkboxgroup" || xtype == "radiogroup")
				valuePart = extGetCheckItemsValue(control, control.submitMode);
			else if (xtype == "treepanel") {
				if (control.canPostValue == null)
					valuePart = extGetTreeValue(control);
			} else if (xtype == "panel") {
				subControl = document.getElementById(namePart + "__dst");
				if (subControl != null)
					valuePart = extGetSelectItemsValue(subControl,
							control.submitMode);
			} else if (xtype == "grid" || xtype == "editorgrid") {
				if (xtype == "editorgrid")
					control.stopEditing();
				valuePart = extGetGridRecords(control);
			}
			if (valuePart != null)
				dataArray[namePart] = valuePart;
		}
	}
	if (control.items != null) {
		control.items.each(function(c) {
			extGetAllControlsValueArray(c, dataArray);
		});
	}
}

function extGetDateTime(dt) {
	var t = "";

	if (dt.timeControl) {
		t = Ext.getCmp(dt.timeControl).getValue();
		if (!IsEmpty(t))
			t = " " + t;
	}
	if (!IsEmpty(dt.getValue()))
		return extFormatDate(dt.getValue()) + t;
	else
		return "";
}

function extGetAllControlsValue(controlList) {
	if (IsEmpty(controlList))
		return;
	var dataArray = {};
	var controls = new Array();
	var i, j;

	controls = controlList.split(',');
	j = controls.length;
	for (i = 0; i < j; i++)
		extGetAllControlsValueArray(Ext.getCmp(controls[i]), dataArray);
	return dataArray;
}

function extFormatDate(dt, hasTime) {
	if (dt != null && !IsEmpty(dt))
		if (hasTime)
			return dt.format("Y-m-d H:i:s");
		else
			return dt.format("Y-m-d");
	else
		return "";
}

function extSetAllControlsValueObj(control, jsonObject, isReset) {
	var namePart, valuePart, xtype, subControl;

	namePart = control.id;
	if (namePart == null)
		valuePart = null;
	else if (isReset)
		valuePart = "";
	else
		valuePart = jsonObject[namePart];
	if (control.getXType != null)
		xtype = control.getXType();
	else
		xtype = null;
	if (valuePart != null) {
		if (xtype == "textfield" || xtype == "numberfield" || xtype == "hidden"
				|| xtype == "textarea" || xtype == "timefield"
				|| xtype == "combo" || xtype == "htmleditor") {
			if (isReset) {
				if (xtype == "textfield" && control.inputType == "file")
					ResetFileUpload(control.el.dom);
				else {
					if (xtype == "combo" && control.mustSelection)
						control.getStore().clearFilter(false);
					control.setValue("");
				}
				control.clearInvalid();
			} else if (xtype != "textfield" || control.inputType != "file")
				control.setValue(valuePart);
		} else if (xtype == "checkbox" && control.simpleCheck != null)
			control.setValue(valuePart == 1 || valuePart == "true");
		else if (xtype == "datefield") {
			if (isReset) {
				control.setValue("");
				//control.setMinValue(null);
				//control.setMaxValue(null);
				control.clearInvalid();
			} else if (control.timeControl == null)
				if (typeof (valuePart) == "object")
					control.setValue(valuePart);
				else {
					var pos = valuePart.indexOf(" ");
					if (pos == -1)
						pos = valuePart.indexOf("T");
					if (pos == -1)
						pos = valuePart.length;
					control.setValue(Date.parseDate(
							valuePart.substring(0, pos), "Y-m-d"));
				}
			else {
				if (typeof (valuePart) == "object") {
					control.setValue(valuePart);
					Ext.getCmp(control.timeControl).setValue(
							valuePart.format("H:i:s"));
				} else {
					var pos = valuePart.indexOf(" ");
					if (pos == -1)
						pos = valuePart.indexOf("T");
					control.setValue(Date.parseDate(
							valuePart.substring(0, pos), "Y-m-d"));
					Ext.getCmp(control.timeControl).setValue(
							valuePart.substring(pos + 1));
				}
			}
		}/* else if (xtype == "checkboxgroup" || xtype == "radiogroup")
			extSetCheckItemsValue(control, valuePart);
		*/else if (xtype == "treepanel")
			control.selectPath("//" + valuePart, "text");
		else if (xtype == "slider")
			if (isReset)
				control.setValue(control.minValue || 0);
			else
				control.setValue(valuePart || control.minValue);
		else if (xtype == "panel") {
			subControl = document.getElementById(namePart + "__dst");
			if (subControl != null)
				if (isReset)
					ClearListOptions(subControl);
				else
					extSetSelectItemsValue(subControl, valuePart);
		} else if (xtype == "grid" || xtype == "editorgrid") {
			if (xtype == "editorgrid") {
				control.stopEditing();
				control.getStore().rejectChanges();
			}
			if (IsEmpty(valuePart))
				control.getStore().removeAll();
			else
				control.getStore().loadData(Ext.decode(valuePart));
		}
	}
	if (control.items != null && xtype != "checkboxgroup"
			&& xtype != "radiogroup") {
		control.items.each(function(c) {
			extSetAllControlsValueObj(c, jsonObject, isReset);
		});
	}
}

function extSetAllControlsValue(controlList, jsonText, isReset) {
	if (IsEmpty(controlList) || IsEmpty(jsonText))
		return;
	var controls = new Array();
	var i, j;
	var jsonObj = Ext.decode(jsonText);

	controls = controlList.split(',');
	j = controls.length;
	for (i = 0; i < j; i++)
		extSetAllControlsValueObj(Ext.getCmp(controls[i]), jsonObj, isReset);
}

function extVerifyAllControlsObj(control, firstConrol) {
	var c, v, namePart, valuePart, xtype, subControl, con = firstConrol;

	if (control.name != null) {
		if (control.getXType() == "combo") {
			extAddComboHistory(control);
			if (control.forceSelection) {
				if (IsEmpty(control.getValue())) {
					control.setValue("");
					control.el.dom.value = "";
				}
			} else if (control.mustSelection) {
				if (IsEmpty(control.el.dom.value)) {
					control.setValue("");
					control.el.dom.value = "";
				} else {
					v = extGetComboValue(control);
					if (v != null && v != control.getValue())
						control.setValue(v);
				}
			} else if (control.hiddenName)
				control.setValue(control.el.dom.value);
		}
		if (!control.validate() && !con)
			con = control;
	}
	if (control.items != null) {
		control.items.each(function(c) {
			c = extVerifyAllControlsObj(c, con);
			if (c && !con)
				con = c;
		});
	}
	return con;
}

function extVerifyAllControls(controlList) {
	if (IsEmpty(controlList))
		return;
	var controls = new Array();
	var i, j, c, o = null, con = null;
	var result = true;

	controls = controlList.split(',');
	j = controls.length;
	for (i = 0; i < j; i++) {
		c = extVerifyAllControlsObj(Ext.getCmp(controls[i]), null);
		if (c && !con)
			con = c;
	}
	if (con) {
		con.findParentBy(function(p) {
			if (p.getXType() == 'tabpanel') {
				if (p.getActiveTab() != o && o != null)
					p.setActiveTab(o);
			}
			o = p;
		});
		con.focus(true, true);
		return false;
	} else
		return true;
}

function extGetComboValue(control) {
	var r = control.findRecord(control.displayField || control.valueField,
			control.el.dom.value);
	if (r != null)
		return r.get(control.valueField || control.displayField);
	else
		return null;
}

function extResetAllControlsValue(controlList) {
	extSetAllControlsValue(controlList, "{}", true);
}

function extResetForm(controlList) {
	controlList.form.items.each(function(item) {
		   item.reset(); 
		   if (item && item.xtype == 'combo') {
		    item.clearValue();
		   }
		  });
}

function extGetCheckItemsValue(control, submitMode) {
	var items = control.items;
	var i, j = items.getCount();
	var item, isValue;
	var data = new Array();
	var isJson = submitMode == "json" || submitMode == "number";
	var isIndex = submitMode == "index";

	if (j > 0)
		isValue = items.itemAt(0).inputValue != null;
	else
		isValue = false;
	for (i = 0; i < j; i++) {
		item = items.itemAt(i);
		if (item.getValue())
			if (isIndex)
				return i;
			else {
				if (isValue)
					if (isJson)
						data.push(item.inputValue);
					else
						data.push(item.inputValue.replace(/\'/g, "''"));
				else if (isJson)
					data.push(item.boxLabel);
				else
					data.push(item.boxLabel.replace(/\'/g, "''"));
			}
	}
	if (isIndex)
		return -1;
	else if (isJson && submitMode == "json")
		return Ext.encode(data);
	else if (isJson && submitMode == "number")
		return data.join(",");
	else
		return "'" + data.join("','") + "'";
}

function extSetRadioItemsIndex(control, index) {
	var items = control.items;
	var i, j = items.getCount();
	for (i = 0; i < j; i++)
		items.itemAt(i).setValue(i == index);
}

function extGetRadioItemsIndex(control) {
	var items = control.items;
	var i, j = items.getCount();
	for (i = 0; i < j; i++)
		if (items.itemAt(i).getValue())
			return i;
	return -1;
}

function extSetAllScroll(o, f) {
	if (o.fixedLay) {
		if (f) {
			if (!o.autoScroll && !o.originScroll) {
				o.autoScroll = true;
				o.originScroll = true;
				if (Ext.isMac && Ext.isGecko2) {
					o.body.dom.style.overflow = 'hidden';
					(function() {
						o.body.dom.style.overflow = 'auto';
					}).defer(1, this);
				} else
					o.body.dom.style.overflow = "auto";
			}
		} else {
			if (o.originScroll) {
				o.autoScroll = false;
				o.originScroll = false;
				o.body.dom.style.overflow = "hidden";
			}
		}
	} else {
		if (o.items != null) {
			o.items.each(function(c) {
				extSetAllScroll(c, f);
			});
		}
	}
}

function extAdjustWindow(o) {
	if (o.maximized)
		return true;
	var aw = document.body.clientWidth, ah = document.body.clientHeight;
	var sz = o.getSize(), bw = o.width, bh = o.height, flag = true, tp;
	if (aw < bw) {
		if (!o.originWidth)
			o.originWidth = bw;
		extSetAllScroll(o, true);
		o.setWidth(aw - 5);
		o.el.setLeft(0);
		tp = o.el.getTop();
		if (ah >= bh) {
			tp = (ah - bh) / 2;
			o.el.setTop(tp);
		}
		o.setPosition(0, tp);
		flag = false;
	} else {
		if (o.originWidth) {
			o.setWidth(o.originWidth);
			o.originWidth = null;
		}
	}
	if (ah < bh) {
		if (!o.originHeight)
			o.originHeight = bh;
		extSetAllScroll(o, true);
		o.setHeight(ah - 5);
		tp = o.el.getLeft();
		if (aw >= bw) {
			tp = (aw - bw) / 2;
			o.el.setLeft(tp);
		}
		o.el.setTop(0);
		o.setPosition(tp, 0);
		flag = false;
	} else {
		if (o.originHeight) {
			o.setHeight(o.originHeight);
			o.originWidth = null;
		}
	}
	if (aw >= bw && ah >= bh) {
		extSetAllScroll(o, false);

	}
	return flag;
}

function extSetCheckItemsValue(control, value) {
	if (control.submitMode == "index") {
		extSetRadioItemsIndex(control, value);
		return;
	}
	var items = control.items;
	var i, j = items.getCount();
	var item, isValue;

	if (j > 0)
		isValue = items.itemAt(0).inputValue != null;
	else
		isValue = false;
	for (i = 0; i < j; i++) {
		item = items.itemAt(i);
		if (isValue)
			item.setValue(StringInJsonArray(item.inputValue, value));
		else
			item.setValue(StringInJsonArray(item.boxLabel, value));
	}
}

function extGetTreeSelectedNode(tree) {
	return tree.getSelectionModel().getSelectedNode();
}

function extCheckTreeNode(node, checked) {
	if (node.disabled || node.attributes.checked == null
			|| node.parentNode == null)
		return;
	extCheckTreeNodeForce(node, checked);
}

function extCheckTreeNodeForce(node, checked) {
	node.ui.toggleCheck(checked);
	node.attributes.checked = checked;
	node.fireEvent("checkchange", node, checked);
}

function extSimpleToArray(text) {
	return text.substring(1, text.length - 1).split("','");
}

function extSetNodeCheck(node, list) {
	extToggleNodeCheck(node, list.indexOf(node.text) != -1);
	node.eachChild(function(n) {
		extSetNodeCheck(n, list);
	});
}

function extToggleNodeCheck(node, checked) {
	extCheckTreeNode(node, checked);
	if (!node.isLeaf()) {
		node.expand();
		node.eachChild(function(n) {
			extToggleNodeCheck(n, checked);
		});
	}
}

function extLeafToFolderNode(node) {
	if (node.isLeaf())
		node.leaf = false;
	else
		node.eachChild(function(n) {
			extLeafToFolderNode(n);
		});
}

function extLeafToFolder(tree) {
	extLeafToFolderNode(tree.getRootNode());
}

function extRemoveNodeChildren(node) {
	if (node == null)
		return;
	var p, n = node.lastChild;
	while (n != null) {
		p = n.previousSibling;
		extRemoveNodeChildren(n);
		n.remove();
		n = p;
	}
}

function extGetTreeNodeAttributes(node, isSimple) {
	if (isSimple) {
		if (node == null)
			return "";
		else
			return node.text.replace(/\'/g, "''");
	}
	if (node == null)
		return "{}";
	var name, attr = node.attributes;
	var data = new Array();

	for (name in attr) {
		if (name == "leaf" || name == "children")
			break;
		data.push(name + ":" + Ext.encode(attr[name]));
	}
	return "{" + data + "}";
}

function extGetTreeNodeJson(node, data, isRoot) {
	var name, attr = node.attributes;
	var isFirst = true, isChild = node.firstChild == null;

	if (!isRoot) {
		if (node.previousSibling == null)
			data.push("{");
		else
			data.push(",{");
		for (name in attr) {
			if (name != "children" && name != "leaf" && name != "id"
					&& name != "loader") {
				if (isFirst)
					isFirst = false;
				else
					data.push(",");
				data.push(name + ":" + Ext.encode(attr[name]));
			}
		}
	}
	if (isChild) {
		if (!isRoot)
			data.push(",leaf:true");
	} else {
		if (!isRoot)
			data.push(",children:[");
		node.eachChild(function(n) {
			extGetTreeNodeJson(n, data, false);
		});
		if (!isRoot)
			data.push("]");
	}
	if (!isRoot)
		data.push("}");
}

function extGetTreeAllJson(tree) {
	var data = new Array();
	extGetTreeNodeJson(tree.getRootNode(), data, true);
	return "[" + data.join("") + "]";
}

function extGetCheckedAttr(tree, isSimple, origin) {
	var nodes = tree.getChecked();
	var flag = tree.submitDisable == null || tree.submitDisable;
	var data = new Array();
	var i, j = nodes.length;

	for (i = 0; i < j; i++)
		if (flag || !nodes[i].disabled)
			data.push(extGetTreeNodeAttributes(nodes[i], isSimple));
	if (isSimple)
		if (origin)
			return data.join(";");
		else
			return "'" + data.join("','") + "'";
	else
		return "[" + data.join(",") + "]";
}

function extGetTreeSelNodeAttributes(tree, isSimple) {
	var val = extGetTreeNodeAttributes(extGetTreeSelectedNode(tree), isSimple);
	if (isSimple)
		return "'" + val + "'";
	else
		return val;
}

function extGetTreeValue(tree) {
	var mode = tree.submitMode;

	if (mode == "all")
		return extGetTreeAllJson(tree);
	else if (mode == "checked")
		return extGetCheckedAttr(tree, false);
	else if (mode == "checkedSimple")
		return extGetCheckedAttr(tree, true);
	else if (mode == "select")
		return extGetTreeSelNodeAttributes(tree, true);
	else
		return extGetTreeSelNodeAttributes(tree, false);
}

function extGetSelectItemsValue(select, submitMode) {
	var i, j = select.length;
	var data = new Array();
	var isJson = submitMode == "json";

	for (i = 0; i < j; i++)
		if (isJson)
			data.push(select.options[i].value);
		else
			data.push(select.options[i].value.replace(/\'/g, "''"));
	if (isJson)
		return Ext.encode(data);
	else
		return "'" + data.join("','") + "'";
}

function extSetSelectItemsValue(select, valueText) {
	var value = Ext.decode(valueText);
	var i, j = value.length, hasText;

	ClearListOptions(select);
	if (j > 0)
		hasText = value[0]["text"] != null;
	else
		return;
	for (i = 0; i < j; i++)
		if (hasText)
			select.options[i] = new Option(value[i]["text"], value[i]["value"]);
		else
			select.options[i] = new Option(value[i], value[i]);
}

function extRemoveGridSelection(grid) {
	var c, k = 0, store = grid.getStore(), l = 0;
	var deled = false;

	if (grid.getXType() == "editorgrid") {
		var r = extGetGridSelRecord(grid);
		if (r != null) {
			k = store.indexOf(r);
			if (r.dirty)
				r.reject();
			store.remove(r);
			deled = true;
			c = store.getCount();
			if (c > 0) {
				if (k >= c)
					k = c - 1;
				try {
					grid.startEditing(k, 0);
				} catch (e) {
					grid.startEditing(store.getCount() - 1, 0);
				}
			}
			l = -1;
		}
	} else {
		var rows = grid.getSelectionModel().getSelections();
		l = rows.length;
		var i, j = l - 1;
		if (j >= 0) {
			deled = true;
			k = store.indexOf(rows[0]);
		}
		for (i = j; i >= 0; i--)
			store.remove(rows[i]);
		c = store.getCount();
		if (c > 0) {
			if (k >= c)
				k = c - 1;
			grid.getSelectionModel().selectRow(k);
			grid.getView().focusRow(k);
		}
		l = l * -1;
	}
	extSetPageBarText(grid, l);
	return deled;
}

function extGetTreeNodeDisplayPath(node) {
	return node.getPath("text").substring(2);
}

function extShowError(message, handler) {
	Ext.Msg.show( {
		title : '错误',
		msg : message,
		buttons : Ext.Msg.OK,
		fn : handler,
		icon : Ext.MessageBox.ERROR
	});
}

function extGetExceptMsg(msg) {
	var i = msg.indexOf('text:"') + 6;
	return msg.substring(i, msg.indexOf('"', i));
}

function extOpenLogin() {
	Ext.MessageBox.hide();
	SubmitToServer('main?action=webbuilder/system/login.xwl', {
		relogin : 'true'
	}, '重新登录', 'icon_go');
}

function extShowExcept(msg, handler) {
	var m;
	if (msg == null)
		m = "服务器没有响应，请稍候再试。";
	else {
		var i = msg.indexOf('id="bd___except";');
		if (i > 0) {
			i = msg.indexOf("\"font_bold\">");
			var j = msg.indexOf("<br></td>", i);
			if (j == -1)
				j = msg.indexOf("</td>", i);
			m = msg.substring(i + 12, j);
		} else {
			i = msg.indexOf('id="bd___login";');
			if (i > 0)
				m = '系统需要重新登录。<br>点击<a href="javascript:extOpenLogin();">登录</a>在新窗口中打开登录页面。';
			else {
				i = msg.indexOf('id="bd___forbd";');
				if (i > 0)
					m = extGetExceptMsg(msg);
				else {
					i = msg.indexOf('id="bd___invalid";');
					if (i > 0)
						m = extGetExceptMsg(msg);
					else
						m = msg;
				}
			}
		}
	}
	if (IsEmpty(m))
		m = '未知错误。';
	extShowError(m, handler);
}

function extShowMessage(s, handler) {
	Ext.Msg.show( {
		title : '信息',
		msg : s,
		buttons : Ext.Msg.OK,
		fn : handler,
		icon : Ext.MessageBox.INFO
	});
}

function extShowInfo(s, handler) {
	Ext.Msg.show( {
		title : '信息',
		msg : s,
		buttons : Ext.Msg.OK,
		fn : handler
	});
}

function extShowWarning(s, handler) {
	Ext.Msg.show( {
		title : '警告',
		msg : s,
		buttons : Ext.Msg.OK,
		fn : handler,
		icon : Ext.MessageBox.WARNING
	});
}

function extConfirm(s, handler) {
	Ext.Msg.confirm('确认', s, function(btn) {
		if (btn == "yes")
			handler();
	});
}

function extGetLastChild(node) {
	var n = node;
	while (true) {
		if (n.firstChild)
			n = n.firstChild;
		else
			return n;
	}
	return n;
}

function extGetColMap(grid) {
	var config = grid.getColumnModel().config, result = new Array();
	var col, exp, emp = new Array();
	var items = grid.store.fields.items;
	var i, j, k = 0;

	j = items.length;
	for (i = 0; i < j; i++) {
		col = GetObjectFromArray(config, "dataIndex", items[i]["name"]);
		if (col == null || col.hidden)
			continue;
		if (col.mapKey != null)
			result[k] = eval(col.mapKey + "__kdt");
		else
			result[k] = 0;
		if (col.emptyLabel != null)
			emp[k] = col.emptyLabel;
		else
			emp[k] = "";
		k++;
	}
	return emp.join(",") + "=" + Ext.encode(result);
}

function extGetGridFields(grid) {
	var config = grid.getColumnModel().config, result = new Array();
	var col, header, exp;
	var items = grid.store.fields.items;
	var i, j, k = 0;

	j = items.length;
	for (i = 0; i < j; i++) {
		col = GetObjectFromArray(config, "dataIndex", items[i]["name"]);
		if (col == null || col.hidden)
			continue;
		header = col.header;
		if (header == null)
			header = "";
		exp = "{name:\"" + col.dataIndex + "\",title:" + Ext.encode(header)
				+ ",type:\"" + items[i]["type"] + "\"";
		if (col.align != null)
			exp += ",align:\"" + col.align + "\"";
		if (col.width != null)
			exp += ",width:\"" + col.width + "\"";
		if (col.format != null)
			exp += ",format:" + Ext.encode(col.format);
		result[k++] = exp + "}";
	}
	return "[" + result.join(",") + "]";
}

function extGetGridSelRecord(grid) {
	if (grid.getXType() == "editorgrid") {
		var cell = grid.getSelectionModel().getSelectedCell();
		if (cell != null)
			return grid.getStore().getAt(cell[0]);
		else
			return null;
	} else {
		var r = grid.getSelectionModel().getSelections();
		if (r && r.length > 0)
			return r[0];
		else
			return null;
	}
}

function extAnchorCenter(container, obj) {
	var c = Ext.getCmp(container);
	var o = Ext.getCmp(obj);
	if (c == null || o == null || c.el == null || o.el == null)
		return;
	var l = (c.el.getWidth() - o.el.getWidth()) / 2;
	if (l < 10)
		l = 10;
	o.setPosition(l, 10);
}

function extCall(control) {
	control.handler.call();
}

function extLoadData(grid, data) {
	var store = grid.getStore();
	var fields = store.fields.items;
	var meta = Ext.data.Record.create(fields);
	var i, j = data.length, record;

	grid.stopEditing();
	store.rejectChanges();
	store.removeAll();
	for (i = 0; i < j; i++) {
		record = new meta(data[i]);
		store.add(record);
	}
	store.commitChanges();
}

function extInsertRecord(store, index) {
	var fields = store.fields.items;
	var meta = Ext.data.Record.create(fields);
	var i, j = fields.length;
	var data = {};

	for (i = 0; i < j; i++)
		data[fields[i].name] = "";
	var record = new meta(data);
	if (store.modified.indexOf(record) == -1)
		store.modified.push(record);
	if (index == null)
		store.add(record);
	else
		store.insert(index, record);
}

function extInsertRecordWt(store, index) {
	var fields = store.fields.items;
	var meta = Ext.data.Record.create(fields);
	var i, j = fields.length;
	var data = {};

	for (i = 0; i < j; i++)
		data[fields[i].name] = "";
	var record = new meta(data);
	if (store.modified.indexOf(record) == -1)
		store.modified.push(record);
	if (index == null)
		store.add(record);
	else
		store.insert(index, record);
	return record;
}


function extSetPageBarText(grid, len) {
	var bar = grid.getBottomToolbar() || grid.getTopToolbar();
	if (bar && bar.displayEl && bar.displayEl.update) {
		bar.store.totalLength = bar.store.totalLength + len;
		var count = bar.store.getCount();
		var msg = count == 0 ? bar.emptyMsg : String.format(bar.displayMsg,
				bar.cursor + 1, bar.cursor + count, bar.store.getTotalCount());
		bar.displayEl.update(msg);
	}
}

function extInsertFromControl(grid, controlList, index) {
	var store = grid.getStore();
	var fields = store.fields.items;
	var meta = Ext.data.Record.create(fields);
	var allVal = extGetAllControlsValue(controlList), setVal = {};
	var i, j = fields.length, k;
	for (i = 0; i < j; i++)
		setVal[fields[i].name] = allVal[fields[i].name];
	var record = new meta(setVal);

	if (store.modified.indexOf(record) == -1)
		store.modified.push(record);
	if (index == null)
		store.add(record);
	else {
		if (index == -1) {
			var sl = grid.getSelectionModel().getSelections();
			if (sl.length > 0)
				index = store.indexOf(sl[0]);
		}
		if (index == -1)
			store.add(record);
		else
			store.insert(index, record);
	}
	record.commit();
	k = store.indexOf(record);
	grid.getSelectionModel().selectRow(k);
	grid.getView().focusRow(k);
	extSetPageBarText(grid, 1);
	return record;
}

function extUpdateFromControl(grid, controlList) {
	var store = grid.getStore();
	var fields = store.fields.items;
	var record = grid.getSelectionModel().getSelected();
	var i, j = fields.length;
	var control, val = extGetAllControlsValue(controlList);

	for (i = 0; i < j; i++) {
		control = Ext.getCmp(fields[i].name);
		if (control != null)
			record.set(fields[i].name, val[control.id]);
	}
	record.commit();
	return record;
}

function extInsertGrid(grid, insert) {
	var index;

	if (insert == null || !insert) {
		index = grid.getStore().getCount();
		extInsertRecord(grid.getStore());
	} else {
		var cell = grid.getSelectionModel().getSelectedCell();
		if (cell == null)
			index = 0;
		else
			index = cell[0];
		extInsertRecord(grid.getStore(), index);
	}
	grid.startEditing(index, 0);
}

function extInsertSeqGrid(grid, insert) {
	var index;

	if (insert == null || !insert) {
		index = grid.getStore().getCount();
		extInsertRecord(grid.getStore());
	} else {
		var cell = grid.getSelectionModel().getSelectedCell();
		if (cell == null)
			index = 0;
		else
			index = cell[0];
		extInsertRecord(grid.getStore(), index);
	}
	grid.startEditing(index, 2);
	return index;
}
function extInsertHeadGrid(grid, insert) {
	var index;

	if (insert == null || !insert) {
		index = 0;
		//index = grid.getStore().getCount();
		extInsertRecord(grid.getStore(), index);
	} else {
		//var cell = grid.getSelectionModel().getSelectedCell();
		//if (cell == null)
			index = 0;
		//else
			//index = cell[0];
		extInsertRecord(grid.getStore(), index);
	}
	grid.startEditing(index, 1);
}

function extTotalStore(store, name) {
	var r = 0;
	store.each(function(d) {
		r += d.data[name] || 0;
	});
	return r;
}

function extGridChanged(grid) {
	return !IsEmpty(grid.getStore().modified)
			|| !IsEmpty(grid.getStore().deleteRecords);
}

function extSetDisabled(control, disabled) {
	if (control.disabled == disabled)
		return;
	if (disabled) {
		control.setDisabled(true);
		//control.setIconClass(control.iconCls + "_ds");
	} else {
		var cls = control.iconCls;
		control.setDisabled(false);
		//control.setIconClass(cls.substring(0, cls.length - 3));
	}
}

function extCommitGrid(grid, revert) {
	if (extGridChanged(grid)) {
		var store = grid.getStore();
		if (revert)
			store.rejectChanges();
		else
			store.commitChanges();
		store.deleteRecords = null;
	}
}

function extGetGridKeyList(grid, keyName, isJson) {
	var titleArray = new Array();
	var rows = grid.getSelectionModel().getSelections();
	var jsonObj;
	var i, j = rows.length;

	for (i = 0; i < j; i++)
		if (isJson) {
			jsonObj = {};
			jsonObj[keyName] = rows[i].get(keyName);
			jsonObj["__update"] = "1";
			titleArray[i] = jsonObj;
		} else
			titleArray[i] = rows[i].get(keyName).replace(/\'/g, "''");
	if (isJson)
		return Ext.encode(titleArray);
	else
		return "'" + titleArray.join("','") + "'";
}

function extSetGridKeyValue(grid, keyName, keyValue) {
	var rows = grid.getSelectionModel().getSelections();
	var i, j = rows.length;

	for (i = 0; i < j; i++) {
		rows[i].set(keyName, keyValue);
		rows[i].commit();
	}
}

function extGridDeleteConfirm(grid, key, handler, keyLabel) {
	var rows = grid.getSelectionModel().getSelections();
	var i, j = rows.length;
	var s, lb;

	if (IsEmpty(keyLabel))
		lb = "删除";
	else
		lb = keyLabel;
	if (j == 0) {
		extShowWarning("请选择一条有效的记录。");
		return;
	}
	if (j == 1 && !IsEmpty(key))
		s = '"' + rows[0].get(key) + '"';
	else
		s = "所选择的 " + j + " 条记录";
	extConfirm("确定要" + lb + s + "？", handler);
}

function extCheckResponse(response) {
	var len = response.responseText.length;

	if (len > 5 && response.responseText.substring(len - 6) == "{@ok@}") {
		response.responseText = response.responseText.substring(0, len - 6);
		return true;
	} else
		return false;
}

function extSelectPathNode(node, path, handler) {
	node.expand(false, false, function(n) {
		var b = false;
		var p = path.toUpperCase();
		var i = p.indexOf("/");
		if (node.getDepth() == 0)
			i = node.firstChild.text.length;
		if (i > -1)
			p = p.substring(0, i);
		node.eachChild(function(n) {
			if (n.text.toUpperCase() == p) {
				b = true;
				var t = path.substring(i + 1);
				if (i > -1 && !IsEmpty(t))
					extSelectPathNode(n, t, handler);
				else {
					n.select();
					handler(true, n);
				}
			}
		});
		if (!b)
			handler(false, node);
	});
}

function extFindPathNode(node, path) {
	var nd = null;
	var p = path.toUpperCase();
	var i = p.indexOf("/");

	if (i > -1)
		p = p.substring(0, i);
	node.eachChild(function(n) {
		if (n.text.toUpperCase() == p) {
			if (i > -1)
				nd = extFindPathNode(n, path.substring(i + 1));
			else {
				nd = n;
				return;
			}
		}
	});
	return nd;
}

function extFindRecord(store, name, text) {
	var a, t = text.toUpperCase();
	if (store.getCount() > 0) {
		store.each(function(d) {
			if (d.data[name].toUpperCase() == t) {
				a = d;
				return false;
			}
		});
	}
	return a;
}

function extValidDateBetween(control) {
	var date = control.getValue();
	if (date) {
		if (control.startDate
				&& (!control.dateMax || (date.getTime() != control.dateMax
						.getTime()))) {
			var start = Ext.getCmp(control.startDate);
			start.setMaxValue(date);
			start.validate();
			control.dateMax = date;
		} else if (control.endDate
				&& (!control.dateMin || (date.getTime() != control.dateMin
						.getTime()))) {
			var end = Ext.getCmp(control.endDate);
			end.setMinValue(date);
			end.validate();
			control.dateMin = date;
		}
	}
}

function extAddHint(id, text, title) {
	new Ext.ToolTip( {
		target : id,
		trackMouse : false,
		draggable : true,
		maxWidth : 200,
		minWidth : 100,
		title : title,
		html : text
	});
}

function extShowTemp(lbl, txt) {
	if (extShowTemp.working)
		return;
	extShowTemp.working = true;
	lbl.setText(txt);
	setTimeout(function() {
		lbl.setText(" ");
	}, 300);
	setTimeout(function() {
		lbl.setText(txt);
	}, 600);
	setTimeout(function() {
		lbl.setText(" ");
	}, 900);
	setTimeout(function() {
		lbl.setText(txt);
	}, 1200);
	setTimeout(function() {
		lbl.setText("");
		extShowTemp.working = false;
	}, 5000);
}

function extInvalidUser(combo) {
	if (combo.el == null)
		return true;
	var s = combo.el.dom.value;
	return !IsEmpty(s) && (s.indexOf("(") == -1 || s.indexOf(")") == -1);
}

function extPopulatePopup(obj, menu) {
	obj.on('contextmenu', function(e) {
		e.preventDefault();
		if (!this.menu)
			this.menu = new Ext.menu.Menu(menu);
		this.menu.showAt(e.getXY());
	});
}

function extDisableAll(control, flag) {
	control.items.each(function(c) {
		c.setDisabled(flag);
	});
}

function extAddComboHistory(combo) {
	var s = combo.el.dom.value;
	if (combo.mustSelection || combo.forceSelection || !combo.editable
			|| IsEmpty(s) || combo.noHistory)
		return;
	var store = combo.store;
	var fields = store.fields.items;
	if (fields.length == 0)
		return;
	var meta = Ext.data.Record.create(fields);
	var data = {};
	data[fields[0].name] = s;
	if (fields.length > 1)
		data[fields[1].name] = s;
	var rec = new meta(data);
	var r = combo.findRecord(combo.displayField || combo.valueField, s);
	if (!r) {
		store.insert(0, rec);
		rec.commit();
	}
}


function getEmpty(value)
{	
	var c=0;
	if(value!=null&&value!='')
	{
		c=value;
	}
	return c;
}
//
function extGetAllGridPrintData(grid, handler) {
	var store = grid.getStore();
	var newReader, oldReader = store.reader;

	if (oldReader.jsonData != null)
		newReader = new Ext.data.JsonReader(oldReader.meta, store.fields.items);
	else if (oldReader.xmlData != null)
		newReader = new Ext.data.XmlReader(oldReader.meta, store.fields.items);
	else
		newReader = new Ext.data.ArrayReader( {}, store.fields.items);
	var newStore = new Ext.data.Store( {
		proxy : store.proxy,
		reader : newReader,
		baseParams : store.baseParams,
		listeners : {
			load : function(control, records, options) {
				var i, j = control.getCount();
				var data = new Array();
				for (i = 0; i < j; i++)
					data.push(control.getAt(i));
				handler(data);
			}
		}
	});
	newStore.load();
}
function printGrid(grid31,getDate) {     
	
	    extGetAllGridPrintData(grid31, getDate);
     	 
       }
     
function b(){


  newwin.document.getElementById('tables').innerHTML=titleHTML;
  newwin.prn3_preview();
  newwin.close();
}



function verifyDatass(){
var f=true,k;
var message='';
var s=grid.getStore();
Ext.each(s.getModifiedRecords(), function(r) {
k=100;
if(!r.get('CARGO_NAM')){k=4;message='货名没有填写 ';}
if(!r.get('CONSIGN_NAM')){k=8;message='委托人没有填写 ';}
if(k!=100)
{var i=s.indexOf(r);

grid.startEditing(i,k);f=false;extShowTemp(errLabel,message);return false;}
});
var s=gridDetail.getStore();

Ext.each(s.getModifiedRecords(), function(r) {
	var i=s.indexOf(r);
	k=100;
	if(r.get('WEIGHT_WGT')!=''&&r.get('PIECES_NUM')>r.get('NUM'))
	{
	message='输入的件数比库存大';
	k=14;
	}
	if(r.get('WEIGHT_WGT')!=''&&r.get('WEIGHT_WGT')>r.get('WGT'))
	{
	message='输入的重量比库存大';
	k=16;
	}
if(k!=100)
{
	gridDetail.startEditing(i,k);f=false;extShowTemp(errLabel,message);return false;}
});
return f;
}
function CHK_CNTRNO(str)
{
		
	var j=0.5,jt=0 ,p1=0,p2=0;
		if(str.length!=11)
		{
			return false;
		}
		if(str.substring(3,4)!='U')
		{
			return false;
		}
		
		for(var i=0;i<10;i++)
		{
			j=j*2;
			var s=str.substring(i,i+1);
			var  asci=s.charCodeAt();
			
			if(asci>64)
			{ 
					p1=asci-55;
					if(p1>10)
					{
						p1=p1+1;
					}
					if(p1>21)
					{
						p1=p1+1;
					}
					if(p1>32)
					{
						p1=p1+1;
					}
			}
			else
			p1=s;
			jt=jt+p1*j;
			p1=0;p2=0;
			
			
		  
		}
		p1=str.substring(10,11);
		p2=jt%11;
		
		if(p2==10)
		{ 
			p2=0;
		}
		if(p1==p2)
		{
			
			return true;
		}
		else
		{   
			return true;
		}

}
String.prototype.replaceAll = function(s1,s2) { 
    return this.replace(new RegExp(s1,"gm"),s2); 
}
	function getPhoneS(str)
	{
		var  arrays=new Array("TELNO","TELNO:","TELNO：","TEL","TEL:","TEL：","T","T:","T：","PHONE","PHONE:","PHONE：","T","T:","T：");
		var  fax='';
		for(var i=0;i<arrays.length;i++){
		var  faxs=getFax(arrays[i],str.replaceAll("	","").replaceAll(" ","").toUpperCase());
		if(faxs)
		
			fax= faxs;
		}
		return fax;
	
	}
 function getFaxS(  str)
 {
	var  arrays=new Array("FAXNO","FAXNO:","FAXNO：","FAX","FAX:","FAX：","F","F:","F：");
	var  fax='';
	for(var i=0;i<arrays.length;i++){
	var  faxs=getFax(arrays[i],str.replaceAll("	","").replaceAll(" ",""));
	if(faxs)
		
		fax= faxs;
	}
	return fax;
 	
 }
 function getFax( tem,   str)
	 {
		if(getStates(tem, str))//表示有值
		{	
			var  index=str.indexOf(tem)+tem.length;
			
			var  sm='';
			var i=1;
			var count=index;
			for(;i<=25;i++)
			{	
				
				 sm=str.substring(count,count+1);
				if(isNaN(sm)&&sm!='-'&&sm!='('&&sm!=')')//表示不是数字 并且也不是-；
				{
						break;
				}
				count=count+1;
				
			}
			return str.substring(index,count);
		
		}
		else {
			return false;
		}
	 }
	 function getStates(tem,   str)
	 {
		var  index=str.indexOf(tem);
		if(index<0)
			return false;
		index+=tem.length;
		var  m=str.substring(index,index+1); 
		var  j=0;
		if(!isNaN(m)||m=='('||m=='\\')//标示是数字
		{
			var  s= str.substring(index,index+25);//截取25个字符判断其中是否有7位数字
			
			for(var i=1;i<=25;i++)
			{
				var sm=s.substring(index,index+1);
				if(!isNaN(sm))//标示是数字 则加1；
				{
					j++;
				}
				else//非数字 则清0；
				{
					j=0;
				}
				if(j==7)
				{
					break;
				}
				index=index+1;
			}
			if(j>=7)
			return true;
			else
				getStates(tem,str.substring(index,str.length) );
			
		}
		else {//非数字
			getStates(tem,str.substring(index,str.length) );
		}
		return false;
	 }


function extSetDefaultDate(dateField1,dateField2)
{
	dateField1.setValue(new Date().add(Date.DAY, -7));
	dateField2.setValue(new Date().add(Date.DAY, 7));
}

function toStdAmount(sAmount)
{
 var sComma = /\,/gi;
 var sResult = sAmount.replace(sComma,"");
 var iDotIndex = sResult.indexOf('.');
 var iLength = sResult.length;
 var toMatchNaNum = /\D/;
 if ((iDotIndex!=-1&&iLength-iDotIndex>3)
  ||toMatchNaNum.test(sResult.slice(iDotIndex+1,iLength)))
  return -1;  //小数点后大于2位数 或 含有非数字字符
 else
 {
//将金额处理为标准的######.##形式 begin
  if (iDotIndex==-1)
   sResult = sResult+'.00';
  else if (iDotIndex==0)
  {
   if (iLength-iDotIndex==1) sResult='0'+sResult+'00';
   if (iLength-iDotIndex==2) sResult='0'+sResult+'0';
   if (iLength-iDotIndex==3) sResult='0'+sResult;
  }
  else
  {
   if (iLength-iDotIndex==2) sResult=sResult+'0';
   if (iLength-iDotIndex==1) sResult=sResult+'00';
  }
//将金额处理为标准的######.##形式 end

//处理金额非前面的0 begin
  var sTemp = "";
  sTemp = sResult.slice(0,iDotIndex);
   
  var iTemp = new Number(sTemp);
  sTemp = iTemp.toString();
  if (sTemp.length>16) return -2;
  iDotIndex = sResult.indexOf('.');
//处理金额非前面的0 end

  sResult = sTemp+sResult.slice(iDotIndex); //返回标准的######.##形式的金额
  return sResult;
 }
}

function getChineseCurrencyString(sAmount)
{
 var value = toStdAmount(sAmount);
 if(value<0) return value;
 var sCN_Num = new Array("零","壹","贰","叁","肆","伍","陆","柒","捌","玖");
 var unit = new Array('元', '万', '亿', '万');
 var subunit = new Array('拾', '佰', '仟');
 var sCNzero = '零';

 var result = "";

 var iDotIndex = value.indexOf('.');

 var sBeforeDot = value.slice(0, iDotIndex);
 var sAfterDot = value.slice(iDotIndex);

 var len = 0;
 //before dot
 len = sBeforeDot.length;
 var i = 0, j = 0, k = 0; //j is use to subunit,k is use to unit
 var oldC = '3';
 var cc = '0';
 result = unit[0] + result;

 var oldHasN = false; 
 var hasN = false;
 var allZero = true;
  
 for (i = 0; i < len; i++) {
  if (j == 0 && i != 0) {
   if (!hasN)
   {
    if ((k % 2) == 0) result = result.slice(1);
   }
   else
   {
    if (oldC == '0') result = sCNzero + result;
   }
   result = unit[k] + result;
   //oldC = '3';
   oldHasN = hasN;
   hasN = false;
  }
  cc = sBeforeDot.charAt(len - i - 1);
  if (oldC == '0' && cc != oldC)
  {
   if (hasN) result = sCNzero + result;
  }
  if (cc != '0')
  {
   if (j != 0)
    result = subunit[j - 1] + result;
   var dig = '0';
   dig = sCN_Num[cc];

   if (dig == '0')
    return false;
   hasN = true;
   allZero = false;
   result = dig + result;
  }
  oldC = cc;
  j++;
  if (j == 4)
  {
   k++;
   j = 0;
  }
 }
 if (allZero) {
  result = "零元";
 }
 else {
  var bb = 0;
  if (!hasN) {
   bb++;
   if (!oldHasN) {
    bb++;
   }
  }
  if (bb != 0)
   result = result.slice(bb);
  if (result.charAt(0) == '零')
   result = result.slice(1);
 }

 //after dot
 sAfterDot = sAfterDot.slice(1);
 len = sAfterDot.length;
 var corn = new Array('0','0');   
 var cornunit = new Array('角', '分');
 var n = 0; //j is use to subunit,k is use to unit
 var dig = '0';
 corn[0] = sAfterDot.charAt(0);
 if (len > 1)
  corn[1] = sAfterDot.charAt(1);
 else
  corn[1] = '0';
 if ((corn[0] ==  '0') && (corn[1] == '0'))
  return result += '整';
 else
  if (allZero) result = "";
 for (i = 0; i < 2; i++)
 {
  var curchar = corn[i];
  dig = sCN_Num[curchar];

  if (i==0)
  {
   if(result!=""||curchar!='0')
    result += dig;
   if(curchar!='0')
   {
    result += cornunit[0];
   }
  }
  if (i==1&&curchar!='0') result = result+dig+cornunit[1];
 }

 return result;
}
function kd(form,amount)
{
 var ss = (eval_r("document."+form+"."+amount)).value;
 var sDispMsg = getChineseCurrencyString(ss);
 if(sDispMsg==-1) sDispMsg="错误的金额!";
 if(sDispMsg==-2) sDispMsg="太长的金额!";
 document.getElementByIdx("dx").innerHTML=sDispMsg;
}

function tomoney(form,txtmoney,hidmoney){
    var tonumber;
    var re = /,/g;
    var txt_money = eval_r("document."+form+"."+txtmoney);
    var hid_money = eval_r("document."+form+"."+hidmoney);
    tonumber = txt_money.value.replace(re,"");

    txt_money.value = "";
   if (tonumber ==""){
   }else if (parseFloat(tonumber)<0){
      alert("输入的金额不能小于0！");
      txt_money.focus();
    } else {
     if (tonumber.indexOf(".") ==-1){
      if(!isRealInteger(tonumber)){
         alert("金额必须为数字");
          txt_money.focus();
      } else if (tonumber <1000){
        txt_money.value = tonumber+".00";
      } else if (tonumber < 1000000){
        txt_money.value += tonumber.slice(0,-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else if (tonumber < 1000000000){
        txt_money.value += tonumber.slice(0,-6)+",";
        txt_money.value += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else if (tonumber < 1000000000000){
        txt_money.value += tonumber.slice(0,-9)+",";
        txt_money.value += tonumber.slice(tonumber.length-9,tonumber.length-6)+",";
        txt_money.value += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      }else if (tonumber <  1000000000000000){
        txt_money.value += tonumber.slice(0,-12)+",";
        txt_money.value += tonumber.slice(tonumber.length-12,tonumber.length-9)+",";
        txt_money.value += tonumber.slice(tonumber.length-9,tonumber.length-6)+",";
        txt_money.value += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else {
        alert("请输入正确的金额！");
        txt_money.focus();   
      }
     } else {
      var numberlen = tonumber.indexOf(".");
      var numberdot = tonumber.slice(0,numberlen);
      var dotnumber = tonumber.slice(numberlen);
      var misnumber = tonumber.slice(numberlen+1,tonumber.length);
      //alert("misnumber.length:"+misnumber.length)
      if(!isRealInteger(numberdot+misnumber)){
         alert("金额必须为数字");
          txt_money.focus();
      }else if (misnumber.length > 2){
        alert("输入金额小数点右边的位数不能超过两位！");
        txt_money.focus();
      }else if(misnumber.length==0){
       if (tonumber <1000){
        txt_money.value = numberdot+dotnumber+"00";
      } else if (tonumber < 1000000){
        txt_money.value += numberdot.slice(0,-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"00";
      } else if (tonumber < 1000000000){
        txt_money.value += numberdot.slice(0,-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"00";
      } else if (tonumber < 1000000000000){
        txt_money.value += numberdot.slice(0,-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"00";

      }else if (tonumber <  1000000000000000){
        txt_money.value += numberdot.slice(0,-12)+",";
        txt_money.value += numberdot.slice(numberdot.length-12,numberdot.length-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"00";

      }  else {
          alert("请输入正确的金额！");
          txt_money.focus(); 
        }
      } else if(misnumber.length==1){
       if (tonumber <1000){
        txt_money.value = numberdot+dotnumber+"0";
      } else if (tonumber < 1000000){
        txt_money.value += numberdot.slice(0,-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";
      } else if (tonumber < 1000000000){
        txt_money.value += numberdot.slice(0,-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";
      } else if (tonumber < 1000000000000){
        txt_money.value += numberdot.slice(0,-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";

      } else if (tonumber < 1000000000000000){
        txt_money.value += numberdot.slice(0,-12)+",";
        txt_money.value += numberdot.slice(numberdot.length-12,numberdot.length-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";

      }  else {
          alert("请输入正确的金额！");
          txt_money.focus(); 
        }
      }else if (tonumber < 1000000000000000){
      if (tonumber <1000){
        txt_money.value = numberdot+dotnumber;
      } else if (tonumber < 1000000){
        txt_money.value += numberdot.slice(0,-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
      } else if (tonumber < 1000000000){
        txt_money.value += numberdot.slice(0,-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
      } else if (tonumber < 1000000000000){
        txt_money.value += numberdot.slice(0,-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;

      }else if (tonumber <  1000000000000000){
        txt_money.value += numberdot.slice(0,-12)+",";
        txt_money.value += numberdot.slice(numberdot.length-12,numberdot.length-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;

      }
      }else {
        alert("请输入正确的金额！");
        txt_money.focus();   
      }
    }
   }

 tonumber = txt_money.value.replace(re,"");
  if (tonumber.indexOf(".")!=-1){
    var numberlen2 = tonumber.indexOf(".");
    var numberdot2 = tonumber.slice(0,numberlen2);   
    var misnumber2 = tonumber.slice(numberlen2+1,tonumber.length);
   
 while(numberdot2.indexOf("0") == 0)  //去掉多余的0
  numberdot2 = numberdot2.slice(1);   
    tonumber =numberdot2 + misnumber2;
    hid_money.value = tonumber;
  } else if (tonumber.indexOf(".")==-1){
    hid_money.value = tonumber;
  }
}

function submoney(form,txtmoney,hidmoney){
    var re = /,/g;
    var money;
    var txt_money = eval_r("document."+form+"."+txtmoney);
    var hid_money = eval_r("document."+form+"."+hidmoney);
 money = txt_money.value.replace(re,"");
 hid_money.value = money*100;
}



function initialMoney(hidMin_money,hidMax_money,txtMin_money,txtMax_money,form){
    var hiddenMin_money = eval_r("document."+form+"."+hidMin_money+".value");
    var hiddenMax_money = eval_r("document."+form+"."+hidMax_money+".value");
    var Min_money = eval_r("document."+form+"."+txtMin_money); 
    var Max_money = eval_r("document."+form+"."+txtMax_money);
    returnMoney(form,hidMin_money,txtMin_money);
    returnMoney(form,hidMax_money,txtMax_money);

 }


function returnMoney(form,hidmoney,txtmoney){
    var tonumber;
    var Smoney;
    var re = /,/g;
    var txt_money = eval_r("document."+form+"."+txtmoney);
    var hid_money = eval_r("document."+form+"."+hidmoney);
   
   
   var num = hid_money.value;
   if (num.length>=3){
      Smoney = num.slice(0,num.length-2)+".";
      Smoney += num.slice(num.length-2,num.length);
   } else if (num.length==1){
      Smoney = "0.0"+num;     
   } else if (num.length==2){
      Smoney = "0."+num;     
   }
  
    tonumber = Smoney.toString();
  
   txt_money.value = "";


   if (parseFloat(tonumber)<0){
      //alert("输入的金额不能小于0！");
     // txt_money.focus();
    } else {
     if (tonumber.indexOf(".") ==-1){
      if (tonumber <1000){
        txt_money.value = tonumber+".00";
      } else if (tonumber < 1000000){
        txt_money.value += tonumber.slice(0,-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else if (tonumber < 1000000000){
        txt_money.value += tonumber.slice(0,-6)+",";
        txt_money.value += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else if (tonumber < 1000000000000){
        txt_money.value += tonumber.slice(0,-9)+",";
        txt_money.value += tonumber.slice(tonumber.length-9,tonumber.length-6)+",";
        txt_money.value += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else if (tonumber < 1000000000000000){
        txt_money.value += tonumber.slice(0,-12)+",";
        txt_money.value += tonumber.slice(tonumber.length-12,tonumber.length-9)+",";
        txt_money.value += tonumber.slice(tonumber.length-9,tonumber.length-6)+",";
        txt_money.value += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money.value += tonumber.slice(tonumber.length-3,tonumber.length)+".00";
      } else {
        //alert("请输入正确的金额！");
        //txt_money.focus();   
      }
     } else {
      var numberlen = tonumber.indexOf(".");
      var numberdot = tonumber.slice(0,numberlen);
      var dotnumber = tonumber.slice(numberlen);
      var misnumber = tonumber.slice(numberlen+1,tonumber.length);
      //alert("misnumber.length:"+misnumber.length)
      if (misnumber.length > 2){
        //alert("输入金额小数点右边的位数不能超过两位！");
        //txt_money.focus();
      } else if(misnumber.length==1){
       if (tonumber <1000){
        txt_money.value = numberdot+dotnumber+"0";
      } else if (tonumber < 1000000){
        txt_money.value += numberdot.slice(0,-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";
      } else if (tonumber < 1000000000){
        txt_money.value += numberdot.slice(0,-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";
      } else if (tonumber < 1000000000000){
        txt_money.value += numberdot.slice(0,-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";

      } else if (tonumber < 1000000000000000){
        txt_money.value += numberdot.slice(0,-12)+",";
        txt_money.value += numberdot.slice(numberdot.length-12,numberdot.length-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber+"0";

      } else {
          //alert("请输入正确的金额！");
          //txt_money.focus(); 
        }
      }else if (tonumber < 1000000000000000){
      if (tonumber <1000){
        txt_money.value = numberdot+dotnumber;
      } else if (tonumber < 1000000){
        txt_money.value += numberdot.slice(0,-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
      } else if (tonumber < 1000000000){
        txt_money.value += numberdot.slice(0,-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
      } else if (tonumber < 1000000000000){
        txt_money.value += numberdot.slice(0,-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;

      }else if (tonumber <  1000000000000000){
        txt_money.value += numberdot.slice(0,-12)+",";
        txt_money.value += numberdot.slice(numberdot.length-12,numberdot.length-9)+",";
        txt_money.value += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
        txt_money.value += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
        txt_money.value += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;

      } }else {
        //alert("请输入正确的金额！");
        //txt_money.focus();   
      }
    }
   }
}


//check positive integer
function isRealInteger(e){
  for(i=0;i<e.length;i++){
    var oneChar=e.charAt(i);
    if(oneChar<'0'||oneChar>'9'){
      return false;
    }
    }
  return true;
}

function tomoneyByCur(txt_money,curType){
    var tonumber;
    var re = /,/g;
    tonumber = txt_money.replace(re,"");
    txt_money = "";
 
 var dotNum;//此币种小数点后有几位

 for(var i=0;i<jCurrencyInfo.len();i++)
 {  
  if(jCurrencyInfo.elementAt(i).CurrSign == curType)
  {
     dotNum = jCurrencyInfo.elementAt(i).CurrAmtAvail;
     break;
    }
    
 }

  
   if (tonumber ==""){
   }else if (parseFloat(tonumber)<0){
      alert("输入的金额不能小于0！");
      //txt_money.focus();
      return "";
    } else {
     if (tonumber.indexOf(".") ==-1){ //如果金额中没有小数点
      if (tonumber <1000){
    txt_money = tonumber;
  if(dotNum != 0)
  {
   txt_money = tonumber+".";
   for(var i=0;i<dotNum;i++)//小数点后补0
          txt_money += "0";
  }
      } else if (tonumber < 1000000 ){
        txt_money += tonumber.slice(0,-3)+",";
        txt_money += tonumber.slice(tonumber.length-3,tonumber.length);
  if(dotNum !=0 )
  {
   txt_money += ".";
   for(var i=0;i<dotNum;i++)//小数点后补0
          txt_money += "0";
  }
      } else if (tonumber < 1000000000 ){
        txt_money += tonumber.slice(0,-6)+",";
        txt_money += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money += tonumber.slice(tonumber.length-3,tonumber.length);
  if(dotNum !=0 )
  {
   txt_money += ".";
   for(var i=0;i<dotNum;i++)//小数点后补0
          txt_money += "0";
  }
      } else if (tonumber <= 10000000000 ){
        txt_money += tonumber.slice(0,-9)+",";
        txt_money += tonumber.slice(tonumber.length-9,tonumber.length-6)+",";
        txt_money += tonumber.slice(tonumber.length-6,tonumber.length-3)+",";
        txt_money += tonumber.slice(tonumber.length-3,tonumber.length);
  if(dotNum !=0 )
  {
   txt_money += ".";
   for(var i=0;i<dotNum;i++)//小数点后补0
          txt_money += "0";
  }
      } else {
        alert("请输入正确的金额！");
        //txt_money.focus();   
        return "";
      }
    
  }//如果金额中没有小数点结束
 
 else
 {//输入金额含小数点
      var numberlen = tonumber.indexOf(".");
      var numberdot = tonumber.slice(0,numberlen);//小数点前面的数字
      var dotnumber = tonumber.slice(numberlen);//小数点加后面的数字
      var misnumber = tonumber.slice(numberlen+1,tonumber.length);//小数点后面的数字
     
      if (misnumber.length > dotNum){
        alert("输入金额小数点右边的位数不能超过"+dotNum+"位！");
        //txt_money.focus();
        return "";
      }
   else
   {
    for(var i=0;i<dotNum-misnumber.length;i++)//差几个0补几个0，完美小数点加后面的部分
     dotnumber += "0";
    
    if (tonumber <1000){
     txt_money = numberdot+dotnumber;
      } else if (tonumber < 1000000){
     txt_money += numberdot.slice(0,-3)+",";
     txt_money += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
      } else if (tonumber < 1000000000){
     txt_money += numberdot.slice(0,-6)+",";
     txt_money += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
     txt_money += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
      } else if (tonumber <= 10000000000){
     txt_money += numberdot.slice(0,-9)+",";
     txt_money += numberdot.slice(numberdot.length-9,numberdot.length-6)+",";
     txt_money += numberdot.slice(numberdot.length-6,numberdot.length-3)+",";
     txt_money += numberdot.slice(numberdot.length-3,numberdot.length)+dotnumber;
   
      } else {
     alert("请输入正确的金额！");
     //txt_money.focus(); 
                                        return "";
      }
  }
  
    } //输入金额含小数点结束
}//最顶层else结束
  return txt_money;
}
