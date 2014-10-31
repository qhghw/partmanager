
var createStockDropDown=function(id){  
        var stockname="";  
        var itemno="";  
        var spec="";  
        var innerId="";  
        var tpl='<tpl for="."><div style="height:200px"><div id="'+id+'"></div></div></tpl>';  
        this.StockDropDown = new Ext.form.ComboBox({    
        store:new Ext.data.SimpleStore({fields:['value','text'],data:[['','']]}),    
        editable:true,  
        mode: 'local',  
        listWidth:450,  
        triggerAction:'all',   
        maxHeight: 300,  
        valueField:'value',  
        displayField:'text', 
        editable:false,
        tpl: tpl,  
        selectedClass:'',    
        onSelect:Ext.emptyFn  
        });  
          
        this.StockDropDown.getItemNo=function()  
        {  
            return itemno;  
        };  
          
        this.StockDropDown.getStockName=function()  
        {  
            return stockname;  
        };  
        this.StockDropDown.getSpec=function()  
        {  
            return spec;  
        };  
      
        var StockDropDowncm = new Ext.grid.ColumnModel([  
            {header:'货物代码',dataIndex:'CARGO_NAM_COD'},  
            {header:'货物名称',dataIndex:'CARGO_NAM'},
            {header:'种类代码',dataIndex:'CARGO_KIND_COD'},  
            {header:'种类名称',dataIndex:'CARGO_KIND_NAM'}
        ]);  
        var StockDropDownds = new Ext.data.Store({  
            proxy: new Ext.data.HttpProxy({url:'main?action=webbuilder/application/jtport/cargoOperate/ship_cargo_bill/cargo.xwl'}),  
            reader: new Ext.data.JsonReader({   
                totalProperty:'totalProperty',  
                root:'root'},  
            [  
             
                {name: 'CARGO_NAM_COD'},  
                {name: 'CARGO_NAM'}  ,
                {name: 'CARGO_KIND_COD'},  
                {name: 'CARGO_KIND_NAM'}  ])  
        });  
      
var tftool=new Ext.Toolbar({id:"tfool",items:[
{id:"labelShip",html:"&nbsp;名称:&nbsp;",xtype:"label"}
,{id:"begdate122",name:"cargoNam",xtype:"textfield"}

,{id:"queryMenu",text:"查询",handler:function(control,event){ 
var p=new Object();
p['cargoNam'] = Ext.get("cargoNam").getValue();

StockDropDownds.baseParams = p;
StockDropDownds.load();
},iconCls:"icon_find"}
]});
        var StockDropDownPanel=new Ext.grid.GridPanel({  
                ds: StockDropDownds,  
                cm: StockDropDowncm,  
                sm: new Ext.grid.RowSelectionModel({singleSelect:true}),  
                title:'',  
                tbar:tftool,
                region:'center',  
                height:300,  
                bbar:new Ext.PagingToolbar({  
                    pageSize:20,  
                    store:StockDropDownds,  
                    displayInfo:false,  
                    displayMsg:'显示第{1}条记录,一共有{2}条',  
                    emptyMsg:'没有记录'  
                })  
        });  
        StockDropDown.on("expand",function(){  
            StockDropDownPanel.render(id);  
            StockDropDownds.load({params:{start:0,limit:20}});  
        });  
       StockDropDownPanel.on('rowclick', function(grids, rowIndex, event) {  
    		r=extGetGridSelRecord(gridDetail)
    		if(r){
    			alert('已经存在值，不允许修改了。');
    			return;
    		}
            var sm=grids.getSelectionModel();  
            var record=sm.getSelected();  
            itemno=record.get("CARGO_NAM");  
            var r=extGetGridSelRecord(grid); 
          
            r.set('CARGO_NAM_COD',record.get("CARGO_NAM_COD"));
            r.set('CARGO_KIND_COD',record.get("CARGO_KIND_COD"));
            r.set('CARGO_KIND_NAM',record.get("CARGO_KIND_NAM"));
            
            innerId=record.get("id");  
            StockDropDown.setValue(itemno);  
           
            StockDropDown.collapse();  
        });    
        return StockDropDown;  
     };
     
     
     
     
     
     
     
     
     
     
  ///22222222222222222222222222
     

     var createStockDropDown2=function(id){  
             var stockname="";  
             var itemno="";  
             var spec="";  
             var innerId="";  
             var tpl='<tpl for="."><div style="height:200px"><div id="'+id+'"></div></div></tpl>';  
             this.StockDropDown2 = new Ext.form.ComboBox({    
             store:new Ext.data.SimpleStore({fields:['value','text'],data:[['','']]}),    
             editable:true,  
             mode: 'local',  
             listWidth:300,  
             triggerAction:'all',   
             maxHeight: 350,  
             valueField:'value',  
             displayField:'text',  
             tpl: tpl,  
             editable:false,
             selectedClass:'',    
             onSelect:Ext.emptyFn  
             });  
               
             this.StockDropDown2.getItemNo=function()  
             {  
                 return itemno;  
             };  
               
             this.StockDropDown2.getStockName=function()  
             {  
                 return stockname;  
             };  
             this.StockDropDown2.getSpec=function()  
             {  
                 return spec;  
             };  
           
             var StockDropDowncm = new Ext.grid.ColumnModel([  
                 {header:'客户代码',dataIndex:'SHIPPER_COD'},  
                 {header:'客户名称',dataIndex:'SHIPPER_DOC'}
                 
             ]);  
             var StockDropDownds = new Ext.data.Store({  
                 proxy: new Ext.data.HttpProxy({url:'main?action=webbuilder/application/jtport/cargoOperate/ship_cargo_bill/client1.xwl'}),  
                 reader: new Ext.data.JsonReader({   
                     totalProperty:'totalProperty',  
                     root:'root'},  
                 [  

                     {name: 'SHIPPER_COD'},  
                     {name: 'SHIPPER_DOC'}  
                     ])  
             });  
           
     var tftool=new Ext.Toolbar({id:"tfool",items:[
     {id:"labelShip",html:"&nbsp;名称:&nbsp;",xtype:"label"}
     ,{id:"begdate122",name:"clientNam1",xtype:"textfield"}

     ,{id:"queryMenu",text:"查询",handler:function(control,event){ 
     var p=new Object();
     p['clientNam1'] = Ext.get("clientNam1").getValue();

     StockDropDownds.baseParams = p;
     StockDropDownds.load();
     },iconCls:"icon_find"}
     ]});
             var StockDropDownPanel=new Ext.grid.GridPanel({  
                     ds: StockDropDownds,  
                     cm: StockDropDowncm,  
                     sm: new Ext.grid.RowSelectionModel({singleSelect:true}),  
                     title:'',  
                     tbar:tftool,
                     region:'center',  
                     height:300,  
                     bbar:new Ext.PagingToolbar({  
                         pageSize:20,  
                         store:StockDropDownds,  
                         displayInfo:false,  
                         displayMsg:'显示第{1}条记录,一共有{2}条',  
                         emptyMsg:'没有记录'  
                     })  
             });  
             StockDropDown2.on("expand",function(){  
                 StockDropDownPanel.render(id);  
                 StockDropDownds.load({params:{start:0,limit:20}});  
             });  
            StockDropDownPanel.on('rowclick', function(grids, rowIndex, event) {  
                 var sm=grids.getSelectionModel();  
                 var record=sm.getSelected();  
                 itemno=record.get("SHIPPER_DOC");  
                 var r=extGetGridSelRecord(grid); 
                 if(id=="StockDropDownID2")
                 r.set('SETOWNER_COD',record.get("SHIPPER_COD"));//发货人
                 if(id=="StockDropDownID3")
                 r.set('GETOWNER_COD',record.get("SHIPPER_COD"));//收货人
                 if(id=="StockDropDownID4")
                 r.set('CONSIGN_COD',record.get("SHIPPER_COD"));//委托人
                 if(id=="StockDropDownID5")
                 r.set('PAYER_COD',record.get("SHIPPER_COD"));//付款人
                 innerId=record.get("id");  
                 StockDropDown2.setValue(itemno);  
                 StockDropDown2.collapse();  
             });    
             return StockDropDown2;  
          };
          
          
          
          
          
          
          
          
//33333333333333333333333
          var createStockDropDown3=function(id){  
                  var stockname="";  
                  var itemno="";  
                  var spec="";  
                  var innerId="";  
                  var tpl='<tpl for="."><div style="height:200px"><div id="'+id+'"></div></div></tpl>';  
                  this.StockDropDown3 = new Ext.form.ComboBox({    
                  store:new Ext.data.SimpleStore({fields:['value','text'],data:[['','']]}),    
                  editable:true,  
                  mode: 'local',  
                  listWidth:300,  
                  triggerAction:'all',   
                  maxHeight: 320,  
                  valueField:'value',  
                  displayField:'text',  
                  tpl: tpl,  
                  editable:false,
                  selectedClass:'',    
                  onSelect:Ext.emptyFn  
                  });  
                    
                  this.StockDropDown3.getItemNo=function()  
                  {  
                      return itemno;  
                  };  
                    
                  this.StockDropDown3.getStockName=function()  
                  {  
                      return stockname;  
                  };  
                  this.StockDropDown3.getSpec=function()  
                  {  
                      return spec;  
                  };  
                
                  var StockDropDowncm = new Ext.grid.ColumnModel([  
                      {header:'客户代码',dataIndex:'SHIPPER_COD'},  
                      {header:'客户名称',dataIndex:'SHIPPER_DOC'}
                      
                  ]);  
                  var StockDropDownds = new Ext.data.Store({  
                      proxy: new Ext.data.HttpProxy({url:'main?action=webbuilder/application/jtport/cargoOperate/ship_cargo_bill/client2.xwl'}),  
                      reader: new Ext.data.JsonReader({   
                          totalProperty:'totalProperty',  
                          root:'root'},  
                      [  

                          {name: 'SHIPPER_COD'},  
                          {name: 'SHIPPER_DOC'}  
                          ])  
                  });  
                
          var tftool=new Ext.Toolbar({id:"tfool",items:[
          {id:"labelShip",html:"&nbsp;名称:&nbsp;",xtype:"label"}
          ,{id:"begdate122",name:"clientNam2",xtype:"textfield"}

          ,{id:"queryMenu",text:"查询",handler:function(control,event){ 
          var p=new Object();
          p['clientNam2'] = Ext.get("clientNam2").getValue();

          StockDropDownds.baseParams = p;
          StockDropDownds.load();
          },iconCls:"icon_find"}
          ]});
                  var StockDropDownPanel=new Ext.grid.GridPanel({  
                          ds: StockDropDownds,  
                          cm: StockDropDowncm,  
                          sm: new Ext.grid.RowSelectionModel({singleSelect:true}),  
                          title:'',  
                          tbar:tftool,
                          region:'center',  
                          height:300,  
                          bbar:new Ext.PagingToolbar({  
                              pageSize:20,  
                              store:StockDropDownds,  
                              displayInfo:false,  
                              displayMsg:'显示第{1}条记录,一共有{2}条',  
                              emptyMsg:'没有记录'  
                          })  
                  });  
                  StockDropDown3.on("expand",function(){  
                      StockDropDownPanel.render(id);  
                      StockDropDownds.load({params:{start:0,limit:20}});  
                  });  
                 StockDropDownPanel.on('rowclick', function(grids, rowIndex, event) {  
                      var sm=grids.getSelectionModel();  
                      var record=sm.getSelected();  
                      itemno=record.get("SHIPPER_DOC");  
                      var r=extGetGridSelRecord(grid); 
                      if(id=="StockDropDownID2")
                      r.set('SETOWNER_COD',record.get("SHIPPER_COD"));//发货人
                      if(id=="StockDropDownID3")
                      r.set('GETOWNER_COD',record.get("SHIPPER_COD"));//收货人
                      if(id=="StockDropDownID4")
                      r.set('CONSIGN_COD',record.get("SHIPPER_COD"));//委托人
                      if(id=="StockDropDownID5")
                      r.set('PAYER_COD',record.get("SHIPPER_COD"));//付款人
                      innerId=record.get("id");  
                      StockDropDown3.setValue(itemno);  
                      StockDropDown3.collapse();  
                  });    
                  return StockDropDown3;  
               };
               
               
               
               
//55555555555555555555555555
               var createStockDropDown5=function(id){  
                       var stockname="";  
                       var itemno="";  
                       var spec="";  
                       var innerId="";  
                       var tpl='<tpl for="."><div style="height:200px"><div id="'+id+'"></div></div></tpl>';  
                       this.StockDropDown5= new Ext.form.ComboBox({    
                       store:new Ext.data.SimpleStore({fields:['value','text'],data:[['','']]}),    
                       editable:true,  
                       mode: 'local',  
                       listWidth:300,  
                       triggerAction:'all',   
                       maxHeight: 320,  
                       valueField:'value',  
                       displayField:'text',  
                       tpl: tpl,  
                       editable:false,
                       selectedClass:'',    
                       onSelect:Ext.emptyFn  
                       });  
                         
                       this.StockDropDown5.getItemNo=function()  
                       {  
                           return itemno;  
                       };  
                         
                       this.StockDropDown5.getStockName=function()  
                       {  
                           return stockname;  
                       };  
                       this.StockDropDown5.getSpec=function()  
                       {  
                           return spec;  
                       };  
                     
                       var StockDropDowncm = new Ext.grid.ColumnModel([  
                           {header:'客户代码',dataIndex:'SHIPPER_COD'},  
                           {header:'客户名称',dataIndex:'SHIPPER_DOC'}
                           
                       ]);  
                       var StockDropDownds = new Ext.data.Store({  
                           proxy: new Ext.data.HttpProxy({url:'main?action=webbuilder/application/jtport/cargoOperate/ship_cargo_bill/client3.xwl'}),  
                           reader: new Ext.data.JsonReader({   
                               totalProperty:'totalProperty',  
                               root:'root'},  
                           [  

                               {name: 'SHIPPER_COD'},  
                               {name: 'SHIPPER_DOC'}  
                               ])  
                       });  
                     
               var tftool=new Ext.Toolbar({id:"tfool",items:[
               {id:"labelShip",html:"&nbsp;名称:&nbsp;",xtype:"label"}
               ,{id:"begdate122",name:"clientNam3",xtype:"textfield"}

               ,{id:"queryMenu",text:"查询",handler:function(control,event){ 
               var p=new Object();
               p['clientNam3'] = Ext.get("clientNam3").getValue();

               StockDropDownds.baseParams = p;
               StockDropDownds.load();
               },iconCls:"icon_find"}
               ]});
                       var StockDropDownPanel=new Ext.grid.GridPanel({  
                               ds: StockDropDownds,  
                               cm: StockDropDowncm,  
                               sm: new Ext.grid.RowSelectionModel({singleSelect:true}),  
                               title:'',  
                               tbar:tftool,
                               region:'center',  
                               height:300,  
                               bbar:new Ext.PagingToolbar({  
                                   pageSize:20,  
                                   store:StockDropDownds,  
                                   displayInfo:false,  
                                   displayMsg:'显示第{1}条记录,一共有{2}条',  
                                   emptyMsg:'没有记录'  
                               })  
                       });  
                       StockDropDown5.on("expand",function(){  
                           StockDropDownPanel.render(id);  
                           StockDropDownds.load({params:{start:0,limit:20}});  
                       });  
                      StockDropDownPanel.on('rowclick', function(grids, rowIndex, event) {  
                           var sm=grids.getSelectionModel();  
                           var record=sm.getSelected();  
                           itemno=record.get("SHIPPER_DOC");  
                           var r=extGetGridSelRecord(grid); 
                           if(id=="StockDropDownID2")
                           r.set('SETOWNER_COD',record.get("SHIPPER_COD"));//发货人
                           if(id=="StockDropDownID3")
                           r.set('GETOWNER_COD',record.get("SHIPPER_COD"));//收货人
                           if(id=="StockDropDownID4")
                           r.set('CONSIGN_COD',record.get("SHIPPER_COD"));//委托人
                           if(id=="StockDropDownID5")
                           r.set('PAYER_COD',record.get("SHIPPER_COD"));//付款人
                           innerId=record.get("id");  
                           StockDropDown5.setValue(itemno);  
                           StockDropDown5.collapse();  
                       });    
                       return StockDropDown5;  
                    };
                    
                    
                    
                    
                    
                    
                    
                    
//444444444444444444444444444444
                    var createStockDropDown4=function(id){  
                            var stockname="";  
                            var itemno="";  
                            var spec="";  
                            var innerId="";  
                            var tpl='<tpl for="."><div style="height:200px"><div id="'+id+'"></div></div></tpl>';  
                            this.StockDropDown4 = new Ext.form.ComboBox({    
                            store:new Ext.data.SimpleStore({fields:['value','text'],data:[['','']]}),    
                            editable:true,  
                            mode: 'local',  
                            listWidth:300,  
                            triggerAction:'all',   
                            maxHeight: 320,  
                            valueField:'value',  
                            displayField:'text',  
                            tpl: tpl,  
                            editable:false,
                            selectedClass:'',    
                            onSelect:Ext.emptyFn  
                            });  
                              
                            this.StockDropDown4.getItemNo=function()  
                            {  
                                return itemno;  
                            };  
                              
                            this.StockDropDown4.getStockName=function()  
                            {  
                                return stockname;  
                            };  
                            this.StockDropDown4.getSpec=function()  
                            {  
                                return spec;  
                            };  
                          
                            var StockDropDowncm = new Ext.grid.ColumnModel([  
                                {header:'客户代码',dataIndex:'SHIPPER_COD'},  
                                {header:'客户名称',dataIndex:'SHIPPER_DOC'}
                                
                            ]);  
                            var StockDropDownds = new Ext.data.Store({  
                                proxy: new Ext.data.HttpProxy({url:'main?action=webbuilder/application/jtport/cargoOperate/ship_cargo_bill/client4.xwl'}),  
                                reader: new Ext.data.JsonReader({   
                                    totalProperty:'totalProperty',  
                                    root:'root'},  
                                [  

                                    {name: 'SHIPPER_COD'},  
                                    {name: 'SHIPPER_DOC'}  
                                    ])  
                            });  
                          
                    var tftool=new Ext.Toolbar({id:"tfool",items:[
                    {id:"labelShip",html:"&nbsp;名称:&nbsp;",xtype:"label"}
                    ,{id:"begdate122",name:"clientNam4",xtype:"textfield"}

                    ,{id:"queryMenu",text:"查询",handler:function(control,event){ 
                    var p=new Object();
                    p['clientNam4'] = Ext.get("clientNam4").getValue();

                    StockDropDownds.baseParams = p;
                    StockDropDownds.load();
                    },iconCls:"icon_find"}
                    ]});
                            var StockDropDownPanel=new Ext.grid.GridPanel({  
                                    ds: StockDropDownds,  
                                    cm: StockDropDowncm,  
                                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),  
                                    title:'',  
                                    tbar:tftool,
                                    region:'center',  
                                    height:300,  
                                    bbar:new Ext.PagingToolbar({  
                                        pageSize:20,  
                                        store:StockDropDownds,  
                                        displayInfo:false,  
                                        displayMsg:'显示第{1}条记录,一共有{2}条',  
                                        emptyMsg:'没有记录'  
                                    })  
                            });  
                            StockDropDown4.on("expand",function(){  
                                StockDropDownPanel.render(id);  
                                StockDropDownds.load({params:{start:0,limit:20}});  
                            });  
                           StockDropDownPanel.on('rowclick', function(grids, rowIndex, event) {  
                                var sm=grids.getSelectionModel();  
                                var record=sm.getSelected();  
                                itemno=record.get("SHIPPER_DOC");  
                                var r=extGetGridSelRecord(grid); 
                                if(id=="StockDropDownID2")
                                r.set('SETOWNER_COD',record.get("SHIPPER_COD"));//发货人
                                if(id=="StockDropDownID3")
                                r.set('GETOWNER_COD',record.get("SHIPPER_COD"));//收货人
                                if(id=="StockDropDownID4")
                                r.set('CONSIGN_COD',record.get("SHIPPER_COD"));//委托人
                                if(id=="StockDropDownID5")
                                r.set('PAYER_COD',record.get("SHIPPER_COD"));//付款人
                                innerId=record.get("id");  
                                StockDropDown4.setValue(itemno);  
                                StockDropDown4.collapse();  
                            });    
                            return StockDropDown4;  
                         };
                         
            
       