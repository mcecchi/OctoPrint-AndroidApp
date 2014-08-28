package android.app.printerapp.library;

import java.io.File;
import android.app.AlertDialog;
import android.app.printerapp.ItemListActivity;
import android.app.printerapp.R;
import android.app.printerapp.devices.DevicesListController;
import android.app.printerapp.devices.database.DatabaseController;
import android.app.printerapp.model.ModelFile;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

/**
 * This class will handle the click events for the library elements
 * @author alberto-baeza
 *
 */
public class StorageOnClickListener implements OnItemClickListener, OnItemLongClickListener{

	LibraryFragment mContext;

	
	public StorageOnClickListener(LibraryFragment context){
		
		this.mContext = context;


	}

	//On long click we'll display the gcodes
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		File f = StorageController.getFileList().get(arg2);
		showOptionDialog(f);

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		

		//Logic for getting file type
		File f = StorageController.getFileList().get(arg2);
		
		//If it's folder open it
		if (f.isDirectory()){
			
			//If it's project folder, send stl
			if (StorageController.isProject(f)){
				
				/*if (((ModelFile)f).getStl()!=null) ItemListActivity.showDetailView(arg2);
				else ItemListActivity.requestOpenFile(((ModelFile)f).getGcodeList());*/
				
				//Show detail view regardless
				ItemListActivity.showDetailView(arg2);
								
				
			} else  {							

				StorageController.reloadFiles(f.getAbsolutePath());				
				mContext.sortAdapter();
				
				
			}							
				
			//If it's not a folder, just send the file
		}else {
			
			if (f.getAbsoluteFile().length()>0){
				ItemListActivity.requestOpenFile(f.getAbsolutePath());
			} else {
				
				Toast.makeText(mContext.getActivity(), R.string.storage_toast_corrupted, Toast.LENGTH_SHORT).show();

			}

		}					
	}
	
	//Show dialog for handling files
	private void showOptionDialog(final File f){
		
		String[] mDialogOptions;
		
		
		if (f.getParent().equals("sd")||f.getParent().equals("witbox")){
			
			mDialogOptions = new String[]{mContext.getResources().getString(R.string.library_option_print)};
		}else {
			mDialogOptions = new String[]{mContext.getResources().getString(R.string.library_option_print),
				mContext.getResources().getString(R.string.library_option_edit),mContext.getResources().getString(R.string.library_option_move),
				mContext.getResources().getString(R.string.delete)};
		}
		
		AlertDialog.Builder adb = new AlertDialog.Builder(mContext.getActivity());	
		adb.setTitle(R.string.library_option_dialog_title);
		
		final AlertDialog ad = adb.create();
		
		adb.setAdapter(new ArrayAdapter<String>(mContext.getActivity(), 
				android.R.layout.simple_list_item_1, mDialogOptions), 
				new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				switch (which){
				
				case 0: //Print / Multiprint
					
					if (f.isDirectory()){
						if (StorageController.isProject(f)){
							
							ItemListActivity.requestOpenFile(((ModelFile)f).getStl());
						}
						
					} else {
						DevicesListController.selectPrinter(mContext.getActivity(),f);
					}
					
					
					
					break;
				case 1: //Edit
					
					//TODO Doesn't work when empty gcodes comeon
					ad.dismiss();
					
					if (f.isDirectory()){
						if (StorageController.isProject(f)){
							
							ItemListActivity.requestOpenFile(((ModelFile)f).getStl());
						}
						
					} else {
						//Check if the gcode is empty, won't work if file is actually corrupted
						if (f.getAbsoluteFile().length()>0){
							ItemListActivity.requestOpenFile(f.getAbsolutePath());
						} else {
							
							Toast.makeText(mContext.getActivity(), R.string.storage_toast_corrupted, Toast.LENGTH_SHORT).show();

						}
					}
					
					
					
					
					break;
					
					
				case 2: //Move

					mContext.setMoveFile(f);
					Toast.makeText(mContext.getActivity(), R.string.library_paste_toast, Toast.LENGTH_SHORT).show();

					
					break;
					
				case 3: //Delete
					
					AlertDialog.Builder adb_delete = new AlertDialog.Builder(mContext.getActivity());
					adb_delete.setTitle(R.string.library_delete_dialog_title);
					adb_delete.setMessage(f.getName());
					
					adb_delete.setPositiveButton(R.string.delete, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {

							deleteFiles(f);
							StorageController.getFileList().remove(f);
							
							if (DatabaseController.isFavorite(f.getName())){
								
								Log.i("OUT", "oh my, IT IS! " + f.getName());
								
								DatabaseController.handleFavorite(f, false	);
							}
							
							mContext.notifyAdapter();
							
						}
					});
					
					adb_delete.setNegativeButton(R.string.cancel, null);
					
					adb_delete.show();
					
					break;
				
				}
				
				//ad.dismiss();
				
			}
		});
		
		adb.show();
		
	}
	
	/**
	 * Delete files recursively
	 * @param file
	 */
	public void deleteFiles(File file){
		
		
		if (file.isDirectory()){
			
			for (File f : file.listFiles()){
				
				deleteFiles(f);
				
			}
			
		} 
		
		file.delete();
		
	}
	

	/*private void showGcodeList(File f){
		
		AlertDialog.Builder adb = new AlertDialog.Builder(mContext.getActivity());
		adb.setTitle("Files...");
		
		//We need the alertdialog instance to dismiss it
		final AlertDialog ad = adb.create();
		
		String path = ((ModelFile)f).getGcodeList();
			
		//TODO add popup
		if (path!=null) {
			
			
			final File[] files = (new File(path)).getParentFile().listFiles();
			
			//Create a string-only array for the adapter
			if (files!=null){
				String[] names = new String[files.length];
				
				for (int i = 0 ; i< files.length ; i++){
					
					names[i] = files[i].getName();
					Log.i("OUT","Found " + files[i].getName());
					
				}
					
				adb.setAdapter(new ArrayAdapter<String>(mContext.getActivity(),
						android.R.layout.simple_list_item_1,names), 
						new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						    File m = files[which];

						    //Open desired file
						    //ItemListActivity.requestOpenFile(m.getAbsolutePath());
						    
						    showOptionDialog(m);
						    
						    ad.dismiss();
						}
					});
				
					
			} else Log.i("OUT","Pero si soy null primo");
		}
					
		adb.show();
		
	}*/
		

}