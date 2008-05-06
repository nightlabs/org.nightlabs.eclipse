FCKConfig.UIBridgeBaseUrl = '${baseUrl}';

FCKConfig.AutoDetectLanguage = false ;
FCKConfig.DefaultLanguage = '${language}' ;
FCKConfig.SkinPath = '${baseUrl}/fckeditor-skin/';
FCKConfig.ToolbarSets["Default"] = [
                                	['Source','-','Save','-','Templates'],
                                	['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],
                                	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
                                	'/',
                                	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
                                	['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],
                                	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
                                	['Link','Unlink','Anchor'],
                                	['Image','Table','Rule','Smiley','SpecialChar','PageBreak'],
                                	'/',
                                	['Style','FontFormat','FontName','FontSize'],
                                	['TextColor','BGColor'],
                                	['ShowBlocks'],
                                	'/',
                                	['UIBridge_InsertImage']
                                ] ;
FCKConfig.LinkBrowserURL = '${baseUrl}/nlfinder/nlfinder.html' ;
FCKConfig.ImageBrowserURL = '${baseUrl}/nlfinder/nlfinder.html?type=Images' ;
FCKConfig.FlashBrowserURL = '${baseUrl}/nlfinder/nlfinder.html?type=Flash' ;
FCKConfig.LinkUploadURL = '${baseUrl}/nlfinder/upload.html?command=QuickUpload&type=Files' ;
FCKConfig.ImageUploadURL = '${baseUrl}/nlfinder/upload.html?command=QuickUpload&type=Images' ;
FCKConfig.FlashUploadURL = '${baseUrl}/nlfinder/upload.html?command=QuickUpload&type=Flash' ;
	
	
// Change the default plugin path.
FCKConfig.PluginsPath = '${baseUrl}/fckeditor-custom/plugins';

// Add our plugin to the plugins list.
// FCKConfig.Plugins.Add( pluginName, availableLanguages )
// pluginName: The plugin name. The plugin directory must match this name.
// availableLanguages: a list of available language files for the plugin (separated by a comma).
//FCKConfig.Plugins.Add( 'uibridge', 'en,it,fr' ) ;
FCKConfig.Plugins.Add( 'uibridge', 'en' ) ;
