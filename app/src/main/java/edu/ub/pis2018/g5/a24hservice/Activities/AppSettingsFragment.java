package edu.ub.pis2018.g5.a24hservice.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import Controller.Controller;
import Model.FavoriteZone;
import edu.ub.pis2018.g5.a24hservice.R;

import static android.app.Activity.RESULT_OK;

public class AppSettingsFragment extends PreferenceFragmentCompat{
    Controller controller;
    private static final int CHOOSE_IMAGE =4 ;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_app_settings);
        controller=Controller.getInstance();

        PreferenceCategory userCat= (PreferenceCategory) findPreference("user_category");

        Preference buttonImage = findPreference("change_image");
        buttonImage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showImageChooser();
                return true;
            }
        });
        Preference buttonDelete=findPreference("delete_account");
        buttonDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openDialogDelete();
                return false;
            }
        });
        Preference buttonNewPassword=findPreference("change_password");
        buttonNewPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openDialogNewPassword();
                return false;
            }
        });
        Preference buttonFavZones=findPreference("fav_zones");
        buttonFavZones.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openDialogFavZones();
                return false;
            }
        });
        if(controller.getCurrentUser()==null){
            userCat.setEnabled(false);
            userCat.setVisible(false);
            buttonDelete.setVisible(false);
            buttonImage.setVisible(false);
            buttonNewPassword.setVisible(false);
        }
        else{
            userCat.setEnabled(true);
            userCat.setVisible(true);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Toast.makeText(getActivity(), R.string.image_changed, Toast.LENGTH_SHORT).show();
            controller.changePhotoUri(data);
        }
    }

    private void showImageChooser(){
        Intent intent=new Intent();
        intent.setType(getString(R.string.image_source));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)),CHOOSE_IMAGE);
    }
    private void openDialogDelete(){
        DeleteDialog deleteDialog=new DeleteDialog();
        deleteDialog.show(getFragmentManager(),getString(R.string.delete_account));
    }
    private void openDialogNewPassword(){
        NewPasswordDialog newPasswordDialog=new NewPasswordDialog();
        newPasswordDialog.show(getFragmentManager(),getString(R.string.new_data));
    }
    private void openDialogFavZones(){
        final ArrayList<FavoriteZone> favZ=controller.getCurrentUser().getFavZones();
        final String[] favZList=new String[favZ.size()];
        final boolean[] favZChecked=new boolean[favZ.size()];
        Arrays.fill(favZChecked, false);
        for(int i=0;i<favZ.size();i++)favZList[i]=favZ.get(i).getName();

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(R.string.manage_fav_zones)
                .setMultiChoiceItems(favZList, favZChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                    }
                }).setPositiveButton(R.string.add_fav_zone, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(favZList.length<5){
                            //add fav zone
                            addFavZone();
                        }
                        else
                            Toast.makeText(getContext(), R.string.fav_full, Toast.LENGTH_LONG).show();
                    }
                }).setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //we do nothing
                    }
                }).setNegativeButton(R.string.delete_zone, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j=0;j<favZChecked.length;j++){
                            if(favZChecked[j])favZ.remove(j);
                        }
                        Toast.makeText(getContext(), R.string.fav_deleted, Toast.LENGTH_LONG).show();
                        controller.UploadUserData();
                    }
                }).create();
        dialog.show();
    }
    private void addFavZone(){
        AddFavZoneDialog addFavZoneDialog=new AddFavZoneDialog();
        addFavZoneDialog.show(getFragmentManager(),getString(R.string.add_fav_zone));
    }

    @SuppressLint("ValidFragment")
    public static class DeleteDialog extends AppCompatDialogFragment{
        private EditText editTextEmail;
        private EditText editTextPassword;
        private Controller controller;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            controller=Controller.getInstance();
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.dialog_delete,null);
            builder.setView(view)
                    .setTitle(R.string.delete_login)
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //we do nothing
                        }
                    })
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String email=editTextEmail.getText().toString();
                            String password=editTextPassword.getText().toString();
                            if(!email.isEmpty() && !password.isEmpty())
                                controller.deleteAccount(getContext(),email,password);
                            else
                                Toast.makeText(getContext(), R.string.delete_empty , Toast.LENGTH_SHORT).show();
                        }
                    });
            editTextEmail=view.findViewById(R.id.delete_email);
            editTextPassword=view.findViewById(R.id.delete_password);
            EditText editTextNewPassword=view.findViewById(R.id.delete_pass_new);
            editTextNewPassword.setVisibility(View.GONE);
            return builder.create();
        }
    }
    @SuppressLint("ValidFragment")
    public static class NewPasswordDialog extends AppCompatDialogFragment{
        private EditText editTextEmail;
        private EditText editTextPassword;
        private EditText editTextNewPassword;
        private Controller controller;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            controller=Controller.getInstance();
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.dialog_delete,null);
            builder.setView(view)
                    .setTitle(R.string.delete_login)
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //we do nothing
                        }
                    })
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String email=editTextEmail.getText().toString();
                            String password=editTextPassword.getText().toString();
                            String newPassword=editTextNewPassword.getText().toString();
                            if(!email.isEmpty() && !password.isEmpty() && !newPassword.isEmpty())
                                if(newPassword.length()<6)
                                    Toast.makeText(getContext(), R.string.error_new_password , Toast.LENGTH_SHORT).show();
                                else
                                    controller.changePassword(getContext(),email,password,newPassword);
                            else
                                Toast.makeText(getContext(), R.string.delete_empty , Toast.LENGTH_SHORT).show();
                        }
                    });
            editTextEmail=view.findViewById(R.id.delete_email);
            editTextPassword=view.findViewById(R.id.delete_password);
            editTextNewPassword=view.findViewById(R.id.delete_pass_new);

            return builder.create();
        }
    }
    @SuppressLint("ValidFragment")
    public static class AddFavZoneDialog extends AppCompatDialogFragment{
        private EditText editTextName;
        private TextView lat,lon;
        private Controller controller;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            controller=Controller.getInstance();
            controller.updateCurrentPosition(getContext());
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.dialog_add_fav,null);
            builder.setView(view)
                    .setTitle(R.string.add_fav_zone)
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //we do nothing
                        }
                    })
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name=editTextName.getText().toString();
                            if(!name.isEmpty()) {
                                FavoriteZone fz=new FavoriteZone(name, controller.getCurrentPosition());
                                controller.getCurrentUser().addFavoriteZone(fz);
                                Toast.makeText(getContext(), "Favorite zone added" , Toast.LENGTH_SHORT).show();
                                controller.UploadUserData();
                            }
                            else
                                Toast.makeText(getContext(), R.string.delete_empty , Toast.LENGTH_SHORT).show();
                        }
                    });
            editTextName=view.findViewById(R.id.name);
            lat=view.findViewById(R.id.latValue);
            lon=view.findViewById(R.id.lonValue);
            lat.setText(String.valueOf(controller.getCurrentPosition().getLatitude()));
            lon.setText(String.valueOf(controller.getCurrentPosition().getLongitude()));
            return builder.create();
        }

    }

}

