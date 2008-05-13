var oUIBridgeSetDirtyCommand = new Object() ;
oUIBridgeSetDirtyCommand.Name = 'UIBridge_SetDirty' ;

// This is the standard function used to execute the command (called when clicking in the context menu item).
oUIBridgeSetDirtyCommand.Execute = function()
{
	var xml = new FCKXml() ;
	xml.LoadUrl(FCKConfig.UIBridgeBaseUrl+'/uibridge/setdirty.xml') ;
}

// This is the standard function used to retrieve the command state (it could be disabled for some reason).
oUIBridgeSetDirtyCommand.GetState = function()
{
	// Let's make it always enabled.
	return FCK_TRISTATE_OFF ;
}

FCKCommands.RegisterCommand( oUIBridgeSetDirtyCommand.Name , oUIBridgeSetDirtyCommand ) ;

// ---------------------------------------------

var oUIBridgeImageCommand = new FCKDialogCommand( 'UIBridge_Image', FCKLang.DlgImgTitle, FCKConfig.PluginsPath + 'uibridge/_editor_dialog/fck_image.html', 450, 390 ) ;
FCKCommands.RegisterCommand( 'UIBridge_Image' , oUIBridgeImageCommand ) ;
var oUIBridgeImageItem = new FCKToolbarButton( 'UIBridge_Image', FCKLang.InsertImageLbl, FCKLang.InsertImage, null, false, true, 37 );
FCKToolbarItems.RegisterItem( 'UIBridge_Image', oUIBridgeImageItem );

// ---------------------------------------------

var oUIBridgeLinkCommand = new FCKDialogCommand( 'Link', FCKLang.DlgLnkWindowTitle, FCKConfig.PluginsPath + 'uibridge/_editor_dialog/fck_link.html', 400, 300 ) ;
FCKCommands.RegisterCommand( 'UIBridge_Link' , oUIBridgeLinkCommand ) ;
var oUIBridgeLinkItem = new FCKToolbarButton( 'UIBridge_Link', FCKLang.InsertLinkLbl, FCKLang.InsertLink, null, false, true, 34 );
FCKToolbarItems.RegisterItem( 'UIBridge_Link', oUIBridgeLinkItem );

// ---------------------------------------------

var oUIBridgeFlashCommand = new FCKDialogCommand( 'UIBridge_Flash', FCKLang.DlgImgTitle, FCKConfig.PluginsPath + 'uibridge/_editor_dialog/fck_flash.html', 450, 390 ) ;
FCKCommands.RegisterCommand( 'UIBridge_Flash' , oUIBridgeFlashCommand ) ;
var oUIBridgeFlashItem = new FCKToolbarButton( 'UIBridge_Flash', FCKLang.InsertFlashLbl, FCKLang.InsertFlash, null, false, true, 38 );
FCKToolbarItems.RegisterItem( 'UIBridge_Flash', oUIBridgeFlashItem );



/*
var oUIBridgeInsertImageCommand = new Object() ;
oUIBridgeInsertImageCommand.Name = 'UIBridge_InsertImage' ;

// This is the standard function used to execute the command (called when clicking in the context menu item).
oUIBridgeInsertImageCommand.Execute = function()
{
	//var xml = new FCKXml() ;
	//xml.LoadUrl(FCKConfig.UIBridgeBaseUrl+'/uibridge/insertimage.xml') ;

	var oXmlHttp = FCKTools.CreateXmlObject( 'XmlHttp' ) ;
	oXmlHttp.open( 'GET', FCKConfig.UIBridgeBaseUrl+'/uibridge/insertimage.xml', true ) ;
	// xmlhttp.onreadystatechange=myFunction;
	//	if ( oXmlHttp.status == 200 || oXmlHttp.status == 304 )
	//		oXml = oXmlHttp.responseXML ;
	//	else if ( oXmlHttp.status == 0 && oXmlHttp.readyState == 4 )
	//		oXml = oXmlHttp.responseXML ;
	//	else
	//		oXml = null ;
	oXmlHttp.send( null ) ;
	
	oCommand = new FCKDialogCommand( 'UIBridge_Image', FCKLang.DlgImgTitle, FCKConfig.PluginsPath + 'uibridge/_editor_dialog/fck_image.html', 450, 390 ) ;
	oCommand.Execute();	
}

// This is the standard function used to retrieve the command state (it could be disabled for some reason).
oUIBridgeInsertImageCommand.GetState = function()
{
	// Let's make it always enabled.
	return FCK_TRISTATE_OFF ;
}

FCKCommands.RegisterCommand( oUIBridgeInsertImageCommand.Name , oUIBridgeInsertImageCommand ) ;

var oUIBridgeInsertImageItem = new FCKToolbarButton( oUIBridgeInsertImageCommand.Name, 'Insert Image' ) ;
oUIBridgeInsertImageItem.IconPath = FCKConfig.PluginsPath + 'uibridge/document.png' ;
FCKToolbarItems.RegisterItem( oUIBridgeInsertImageCommand.Name, oUIBridgeInsertImageItem );
*/




/*

// Register the related commands.
FCKCommands.RegisterCommand( 'My_Find' , new FCKDialogCommand( FCKLang['DlgMyFindTitle'] , FCKLang['DlgMyFindTitle'] , FCKConfig.PluginsPath + 'uibridge/test.html' , 340, 170 ) ) ;
FCKCommands.RegisterCommand( 'My_Replace' , new FCKDialogCommand( FCKLang['DlgMyReplaceTitle'], FCKLang['DlgMyReplaceTitle'] , FCKConfig.PluginsPath + 'uibridge/test.html', 340, 200 ) ) ;

// Create the "Find" toolbar button.
var oFindItem = new FCKToolbarButton( 'My_Find', FCKLang['DlgMyFindTitle'] ) ;
oFindItem.IconPath = FCKConfig.PluginsPath + 'uibridge/document.png' ;

FCKToolbarItems.RegisterItem( 'My_Find', oFindItem ) ; // 'My_Find' is the name used in the Toolbar config.

// Create the "Replace" toolbar button.
var oReplaceItem = new FCKToolbarButton( 'My_Replace', FCKLang['DlgMyReplaceTitle'] ) ;
oReplaceItem.IconPath = FCKConfig.PluginsPath + 'uibridge/document.png' ;

FCKToolbarItems.RegisterItem( 'My_Replace', oReplaceItem ) ; // 'My_Replace' is the name used in the Toolbar config.

*/