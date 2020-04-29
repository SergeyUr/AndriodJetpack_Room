package com.appdrafting.mycontacts.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.appdrafting.mycontacts.R;
import com.appdrafting.mycontacts.data.AppDatabase;
import com.appdrafting.mycontacts.data.entity.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rvListContacts) RecyclerView mRVListContacts;
    private AdapterContacts mAdapterContacts;
    private ArrayList<Contact> mListContacts = new ArrayList<>();
    private AppDatabase mAppDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAppDatabase = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "contacts.db"
        )
                .build();

        new GetAllContactAsyncTask().execute();

        mAdapterContacts = new AdapterContacts( this, mListContacts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRVListContacts.setLayoutManager(mLayoutManager);
        mRVListContacts.setItemAnimator(new DefaultItemAnimator());
        mRVListContacts.setAdapter(mAdapterContacts);
    }

    @OnClick(R.id.floatingActionButton)
    public void addCar(View pView) {
        addAndEditContact(false, null, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addAndEditContact(final boolean isUpdate, final Contact pContact, final int pPosition) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_car, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newContactTitle = view.findViewById(R.id.newContactTitle);
        final EditText teFirstName = view.findViewById(R.id.teFirstName);
        final EditText teLastName = view.findViewById(R.id.teLastName);
        final EditText teEmail = view.findViewById(R.id.teEmail);
        final EditText tePhone = view.findViewById(R.id.tePhone);

        newContactTitle.setText(!isUpdate ? "Add Contact" : "Edit Contact");

        if (isUpdate && pContact != null) {
            teFirstName.setText(pContact.getFirstName());
            teLastName.setText(pContact.getLastName());
            teEmail.setText(pContact.getEmail());
            tePhone.setText(pContact.getPhone());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(isUpdate ? "Delete" : "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                if (isUpdate) {

                                    deleteContact(pContact, pPosition);
                                } else {

                                    dialogBox.cancel();

                                }

                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(teFirstName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact First Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(teLastName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact Last Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(teEmail.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tePhone.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact Phone!", Toast.LENGTH_SHORT).show();
                    return;
                }

                alertDialog.dismiss();

                if (isUpdate && pContact != null) {

                    updateCar(
                            teFirstName.getText().toString(),
                            teLastName.getText().toString(),
                            teEmail.getText().toString(),
                            tePhone.getText().toString(),
                            pPosition
                    );
                } else {

                    createCar(
                            teFirstName.getText().toString(),
                            teLastName.getText().toString(),
                            teEmail.getText().toString(),
                            tePhone.getText().toString()
                    );
                }
            }
        });
    }

    private void deleteContact(Contact pContact, int pPosition) {

        mListContacts.remove(pPosition);

        new DeleteContactAsyncTask().execute(pContact);
    }

    private void updateCar(
            String pFirstName,
            String pLastName,
            String pEmail,
            String pPhone,
            int pPosition
    ) {

        Contact vContact = mListContacts.get(pPosition);

        vContact.setFirstName(pFirstName);
        vContact.setLastName(pLastName);
        vContact.setEmail(pEmail);
        vContact.setPhone(pPhone);

        new UpdateContactAsynkTask().execute(vContact);

        mListContacts.set(pPosition, vContact);
    }

    private void createCar(
            String pFirstName,
            String pLastName,
            String pEmail,
            String pPhone
    ) {
        new CreateContactAsynkTask().execute(
                new Contact(pFirstName, pLastName, pEmail, pPhone)
        );
    }

    private class GetAllContactAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... pVoids) {
            mListContacts.addAll(mAppDatabase.getDaoContact().getAllContacts());
            return null;
        }

        @Override
        protected void onPostExecute(Void pVoid) {
            super.onPostExecute(pVoid);

            mAdapterContacts.notifyDataSetChanged();
        }
    }

    private class CreateContactAsynkTask extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... pContacts) {
            long id = mAppDatabase.getDaoContact().addContact( pContacts[0] );


            Contact vContact = mAppDatabase.getDaoContact().getContact(id);

            if (vContact != null) {
                mListContacts.add(0, vContact);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void pVoid) {
            super.onPostExecute(pVoid);

            mAdapterContacts.notifyDataSetChanged();
        }
    }

    private class UpdateContactAsynkTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected void onPostExecute(Void pVoid) {
            super.onPostExecute(pVoid);

            mAdapterContacts.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Contact... pContacts) {
            mAppDatabase.getDaoContact().updateContact(pContacts[0]);

            return null;
        }
    }

    private class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected void onPostExecute(Void pVoid) {
            super.onPostExecute(pVoid);

            mAdapterContacts.notifyDataSetChanged();

        }

        @Override
        protected Void doInBackground(Contact... pContacts) {

            mAppDatabase.getDaoContact().deleteContact(pContacts[0]);
            return null;
        }
    }
}
