package cm.g2i.lalalaworker.ui.fragment;


import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appauthenticator.LaLaLaAuthenticator;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.controllers.gcm.QuickStartPreferences;
import cm.g2i.lalalaworker.controllers.gcm.RegistrationIntentService;
import cm.g2i.lalalaworker.controllers.network.LaLaLaWNetwork;
import cm.g2i.lalalaworker.controllers.network.WorkerController;
import cm.g2i.lalalaworker.controllers.services.FilesUtils;
import cm.g2i.lalalaworker.controllers.settings.Settings;
import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;
import cm.g2i.lalalaworker.ui.activities.ChangePasswdActivity;
import cm.g2i.lalalaworker.ui.activities.CommentsActivity;
import cm.g2i.lalalaworker.ui.activities.EditWorkerInfosActivity;
import cm.g2i.lalalaworker.ui.activities.NewsActivity;
import cm.g2i.lalalaworker.ui.activities.RegisterWorkerActivity;
import cm.g2i.lalalaworker.ui.activities.StrikesActivity;
import cm.g2i.lalalaworker.ui.activities.WorkerAccSettingsActivity;
import cm.g2i.lalalaworker.ui.activities.WorkerWorksActivity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements ErrorFragment.Retry, ProfileImgChooserFragment.ChooseDone{

    private RelativeLayout rlLoading;
    private ProgressBar progressLoading;
    private TextView errorMessage;
    private AppCompatButton reload;

    private RelativeLayout rlHasAccount;
    private RelativeLayout rlHasNoAccount;
    private AppCompatButton createWA;

    private ImageView profile;
    private FloatingActionButton modifyProfile; private ProgressBar modifyProfileProgressBar;

    private TextView name;

    private AppCompatButton modifyPhone;
    private AppCompatTextView phone;
    private AppCompatButton modifyNationality;
    private AppCompatTextView nationality;
    private AppCompatButton modifyTown;
    private AppCompatTextView town;
    private AppCompatButton modifyStreet;
    private AppCompatTextView street;
    private AppCompatButton modifyPasswd;

    private AppCompatRatingBar note;
    private AppCompatButton comments;
    private AppCompatButton works;
    private AppCompatButton strikes;
    private AppCompatButton somenews;
    private AppCompatButton settings;

    private boolean hasAccount = true;

    private Worker worker;
    private Worker workerInLocal;
    private Worker workerInNet;

    private static final int FROM_GALLERY_CODE = 117;
    private static final int TAKE_PHOTO_CODE = 119;
    private Uri imageFileUri = null;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeWidgets(view);
    }

    @Override
    public void onStart(){
        super.onStart();
        VerifyAccountAsyncTask verifyAccountAsyncTask = new VerifyAccountAsyncTask();
        verifyAccountAsyncTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRegistrationBroadcastReceiver!=null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickStartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    public void onPause() {
        if (mRegistrationBroadcastReceiver!=null)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                Toast.makeText(getContext(), R.string.googleplay_error, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    public void sendIdForGCM(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickStartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(getContext(), R.string.gcm_sendTokenSuccess, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.gcm_sendTokenError, Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            try{
                LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter("reg complete string"));
                Intent intent = new Intent(getContext(), RegistrationIntentService.class);
                getContext().startService(intent);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initializeWidgets(View view){
        rlHasAccount = (RelativeLayout) view.findViewById(R.id.frag_account_hasaccount);
        rlHasNoAccount = (RelativeLayout) view.findViewById(R.id.frag_account_rl_hasnoaccount);
        createWA = (AppCompatButton) view.findViewById(R.id.frag_account_create_worker_account);

        profile = (ImageView) view.findViewById(R.id.worker_account_profile_img);
        modifyProfile = (FloatingActionButton) view.findViewById(R.id.modify_profile_img);
        modifyProfileProgressBar = (ProgressBar) view.findViewById(R.id.profile_upload_progress_bar);

        name = (TextView) view.findViewById(R.id.frag_account_name);

        modifyPhone = (AppCompatButton) view.findViewById(R.id.frag_account_edit_phone);
        modifyNationality = (AppCompatButton) view.findViewById(R.id.frag_account_edit_nationality);
        modifyTown = (AppCompatButton) view.findViewById(R.id.frag_account_edit_town);
        modifyStreet = (AppCompatButton) view.findViewById(R.id.frag_account_edit_street);
        phone = (AppCompatTextView) view.findViewById(R.id.frag_account_phone);
        nationality = (AppCompatTextView) view.findViewById(R.id.frag_account_nationality);
        town = (AppCompatTextView) view.findViewById(R.id.frag_account_town);
        street = (AppCompatTextView) view.findViewById(R.id.frag_account_street);
        modifyPasswd = (AppCompatButton) view.findViewById(R.id.frag_account_modify_passwd);

        note = (AppCompatRatingBar) view.findViewById(R.id.frag_account_note);
        works = (AppCompatButton) view.findViewById(R.id.frag_account_works);
        comments = (AppCompatButton) view.findViewById(R.id.frag_account_comments);
        strikes = (AppCompatButton) view.findViewById(R.id.frag_account_strikes);
        somenews = (AppCompatButton) view.findViewById(R.id.frag_account_somenews);
        settings = (AppCompatButton) view.findViewById(R.id.frag_account_settings);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id){
                    case R.id.modify_profile_img:{
                        ProfileImgChooserFragment profileImgChooserFragment = new ProfileImgChooserFragment();
                        profileImgChooserFragment.setTargetFragment(AccountFragment.this, 117);
                        FragmentManager manager = getFragmentManager();
                        profileImgChooserFragment.show(manager, ProfileImgChooserFragment.class.getName());
                    } break;

                    case R.id.frag_account_edit_phone: {
                        Intent intent = new Intent(getContext(), EditWorkerInfosActivity.class);
                        intent.putExtra(Tools.MOD_WHAT, Tools.MOD_PHONE);
                        System.out.println(worker);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_edit_nationality: {
                        Intent intent = new Intent(getContext(), EditWorkerInfosActivity.class);
                        intent.putExtra(Tools.MOD_WHAT, Tools.MOD_NATIONALITY);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_edit_town: {
                        Intent intent = new Intent(getContext(), EditWorkerInfosActivity.class);
                        intent.putExtra(Tools.MOD_WHAT, Tools.MOD_LOCALISATION);
                        intent.putExtra(Tools.MOD_WHAT_LOCAL, Tools.MOD_TOWN);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_edit_street: {
                        Intent intent = new Intent(getContext(), EditWorkerInfosActivity.class);
                        intent.putExtra(Tools.MOD_WHAT, Tools.MOD_LOCALISATION);
                        intent.putExtra(Tools.MOD_WHAT_LOCAL, Tools.MOD_STREET);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_modify_passwd: {
                        Intent intent = new Intent(getContext(), ChangePasswdActivity.class);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        startActivity(intent);
                    } break;

                    case R.id.frag_account_note: {

                    } break;
                    case R.id.frag_account_works: {
                        Intent intent = new Intent(getContext(), WorkerWorksActivity.class);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker.getID());
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_comments: {
                        Intent intent = new Intent(getContext(), CommentsActivity.class);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        intent.putExtra(AccountFragment.class.getName(), true);
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_strikes: {
                        Intent intent = new Intent(getContext(), StrikesActivity.class);
                        intent.putExtra(Tools.WORKER_INTENT_KEY, worker);
                        startActivity(intent);
                    } break;
                    case R.id.frag_account_somenews: {
                        startActivity(new Intent(getContext(), NewsActivity.class));
                    } break;
                    case R.id.frag_account_settings: {
                        startActivity(new Intent(getContext(), WorkerAccSettingsActivity.class));
                    } break;
                }
            }
        };

        modifyProfile.setOnClickListener(listener);
        modifyPhone.setOnClickListener(listener);
        modifyStreet.setOnClickListener(listener);
        modifyTown.setOnClickListener(listener);
        modifyNationality.setOnClickListener(listener);
        modifyPasswd.setOnClickListener(listener);
        note.setOnClickListener(listener);
        works.setOnClickListener(listener);
        comments.setOnClickListener(listener);
        strikes.setOnClickListener(listener);
        somenews.setOnClickListener(listener);
        settings.setOnClickListener(listener);

        createWA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RegisterWorkerActivity.class));
            }
        });

        initializeLoadingView(view);
    }

    public void initializeLoadingView(View view){
        rlLoading = (RelativeLayout) view.findViewById(R.id.loading_layout_rl_loading);
        progressLoading = (ProgressBar) view.findViewById(R.id.loading_layout_progress);
        errorMessage = (TextView) view.findViewById(R.id.loading_layout_error_message);
        reload = (AppCompatButton) view.findViewById(R.id.loading_layout_reload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyAccountAsyncTask verifyAccountAsyncTask = new VerifyAccountAsyncTask();
                verifyAccountAsyncTask.execute();
            }
        });
    }

    private void bindData(){
        name.setText(worker.getName());
        phone.setText(worker.getPhoneNumber());
        nationality.setText(worker.getNationality());
        town.setText(worker.getVille());
        street.setText(worker.getQuartier());
        note.setRating((float)worker.getNote());

        if (worker.getlocalPhoto()==null) Tools.renderProfileImage(profile, Tools.URL_PROFILE_DEFAULT, getContext());
        else Tools.renderProfileImage(profile, worker.getlocalPhoto(), getContext(), true);

        sendIdForGCM();
    }

    private void showSignInDialog(String message){
        ErrorFragment errorFragment = ErrorFragment.newInstance(message);
        FragmentManager manager = getFragmentManager();
        errorFragment.setTargetFragment(AccountFragment.this, 117);
        errorFragment.show(manager, ErrorFragment.class.getName());
    }

    @Override
    public void retry() {
        VerifyAccountAsyncTask verifyAccountAsyncTask = new VerifyAccountAsyncTask();
        verifyAccountAsyncTask.execute();
    }

    @Override
    public void getChoise(int choose) {
        if (choose==ProfileImgChooserFragment.GALLERY_CHOOSE){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getContext().getString(R.string.profile_img_chooser_gallery_title)), FROM_GALLERY_CODE);;
        }
        else if (choose==ProfileImgChooserFragment.TAKE_PHOTO_CHOOSE){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageFileUri = FilesUtils.getOutputImageFileURI(worker.getID());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
            startActivityForResult(intent, TAKE_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FROM_GALLERY_CODE){
            if (resultCode == RESULT_OK && data!=null && data.getData()!=null){
                imageFileUri = data.getData();
                try{
                    String imageFilePath =  FilesUtils.copyToLaLaLaWImagesDirectory(FilesUtils.UriToPath(imageFileUri, getContext()), worker.getID());
                    UploadImageProfileAsyncTask uploadImageProfileAsyncTask = new UploadImageProfileAsyncTask();
                    uploadImageProfileAsyncTask.execute(imageFilePath);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{

            }
        }
        else if (requestCode == TAKE_PHOTO_CODE){
            if (resultCode == RESULT_OK){
                String imageFilePath = imageFileUri.getPath();
                System.out.println(imageFilePath);
                UploadImageProfileAsyncTask uploadImageProfileAsyncTask = new UploadImageProfileAsyncTask();
                uploadImageProfileAsyncTask.execute(imageFilePath);
            }
            else{

            }
        }

    }

    /*class GetAccountLocalAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private Exception exception;
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                Account[] accounts = LaLaLaAuthenticator.getAccountsByType(LaLaLaAuthenticator.WORKER_ACCOUNT_TYPE, getContext());
                workerInLocal = LaLaLaSQLiteOperations.getWorker(getContext());
                return (accounts!=null && accounts.length>0 && workerInLocal!=null);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){

            }
        }
    }*/

    class VerifyAccountAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private Exception exception;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                Account[] accounts = LaLaLaAuthenticator.getAccountsByType(LaLaLaAuthenticator.WORKER_ACCOUNT_TYPE, getContext());
                workerInLocal = LaLaLaSQLiteOperations.getWorker(getContext());
                if (workerInLocal!= null) {
                    workerInNet = WorkerController.getWorkerByID(workerInLocal.getID());
                    String[] workersAccountSettingsInNet = WorkerController.workerAccountSettings(workerInLocal.getID());
                    if (workersAccountSettingsInNet!=null){
                        //System.out.println("dskdslkdjsjdkjsldjksjldjksjldjskdjlsjk");
                        Settings.setWkrsAccSettings(workersAccountSettingsInNet, getContext());
                    }
                    if (workerInLocal!=null && workerInNet!=null){
                        LaLaLaSQLiteOperations.updateWorker(workerInNet, true, getContext());
                        //Toast.makeText(getContext(), workerInNet.getNote()+"", Toast.LENGTH_SHORT).show();
                    }
                    Settings.getWkrsAccSettings(getContext());
                }
                return (accounts!=null && accounts.length>0 && workerInLocal!=null && workerInNet!=null);
            } catch (Exception e){
                exception = e;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rlHasNoAccount.setVisibility(View.INVISIBLE);
            rlHasAccount.setVisibility(View.INVISIBLE);
            rlLoading.setVisibility(View.VISIBLE);
            progressLoading.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.INVISIBLE);
            reload.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                rlHasAccount.setVisibility(View.VISIBLE);
                rlHasNoAccount.setVisibility(View.INVISIBLE);
                rlLoading.setVisibility(View.INVISIBLE);
                worker = workerInLocal;
                bindData();
            }
            else{
                if (exception==null){
                    rlLoading.setVisibility(View.INVISIBLE);
                    rlHasAccount.setVisibility(View.INVISIBLE);
                    rlHasNoAccount.setVisibility(View.VISIBLE);
                }
                if (exception instanceof SocketTimeoutException){
                    rlHasAccount.setVisibility(View.VISIBLE);
                    rlHasNoAccount.setVisibility(View.INVISIBLE);
                    rlLoading.setVisibility(View.INVISIBLE);
                    worker = workerInLocal;
                    bindData();
                    showSignInDialog(getString(R.string.connection_error_message_account));
                }
                else if (exception instanceof IOException){
                    rlHasAccount.setVisibility(View.VISIBLE);
                    rlHasNoAccount.setVisibility(View.INVISIBLE);
                    rlLoading.setVisibility(View.INVISIBLE);
                    worker = workerInLocal;
                    bindData();
                    showSignInDialog(getString(R.string.other_ioexception_message_account));
                }
                else{

                }

            }
        }
    }

    public class UploadImageProfileAsyncTask extends AsyncTask<String, Integer, HashMap<String, String>>{
        private Exception exception;
        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            try{
                worker.setLocalPhoto(strings[0]);
                LaLaLaSQLiteOperations.updateWorker(worker, true, getContext());
                return WorkerController.uploadWorkerProfileImg(strings[0], this);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            modifyProfileProgressBar.setVisibility(View.VISIBLE);
            modifyProfileProgressBar.setIndeterminate(false);
            modifyProfileProgressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            modifyProfileProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
            super.onPostExecute(stringStringHashMap);
            modifyProfileProgressBar.setVisibility(View.INVISIBLE);
            if (stringStringHashMap!=null && !Boolean.parseBoolean(stringStringHashMap.get("error"))){
                Tools.renderProfileImage(profile, worker.getlocalPhoto(), getContext(), true);
                SetWorkerImageProfileAsyncTask setWorkerImageProfileAsyncTask = new SetWorkerImageProfileAsyncTask();
                setWorkerImageProfileAsyncTask.execute(stringStringHashMap.get("file_up_url"));
            }
            else{
                showSignInDialog("Impossible to upload image");
            }
            System.out.println(stringStringHashMap.get("file"));
            System.out.println(stringStringHashMap.get("file_up_url"));
        }

        public void _publishProgress(int progress){
            publishProgress(progress);
        }
    }

    class SetWorkerImageProfileAsyncTask extends AsyncTask<String, Void, Pair<Boolean, String>>{
        private Exception exception;
        @Override
        protected Pair<Boolean, String> doInBackground(String... strings) {
            try{
                String res = WorkerController.setWorkerProfileImg(worker.getID(), strings[0]);
                String[] tab = Tools.split(res, "|");
                return new Pair<>(Boolean.parseBoolean(tab[0]), tab[1]);
            } catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
            return new Pair<>(false, null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            modifyProfileProgressBar.setVisibility(View.VISIBLE);
            modifyProfileProgressBar.setIndeterminate(true);
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> booleanStringPair) {
            super.onPostExecute(booleanStringPair);
            modifyProfileProgressBar.setVisibility(View.INVISIBLE);
            if (booleanStringPair.first){
                Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                if (exception==null){
                    showSignInDialog(booleanStringPair.second);
                    System.out.println("BUZZ ---> "+booleanStringPair.first+":::"+booleanStringPair.second);
                }
                else if (exception instanceof SocketTimeoutException){
                    showSignInDialog(getString(R.string.connection_error_message));
                }
                else{
                    showSignInDialog(getString(R.string.other_ioexception_message));
                }
            }
        }
    }
}
