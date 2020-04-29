package com.appdrafting.mycontacts.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.appdrafting.mycontacts.R;
import com.appdrafting.mycontacts.data.entity.Contact;

import java.util.ArrayList;

public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.MyViewHolder> {

    private MainActivity mActivity;
    private ArrayList<Contact> mListContact;

    public AdapterContacts(MainActivity pMainActivity, ArrayList<Contact> pListContact) {
        mActivity = pMainActivity;
        mListContact = pListContact;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mFirstName;
        public TextView mLastName;
        public TextView mEmail;
        public TextView mPhone;

        public MyViewHolder(View view) {
            super(view);

            mFirstName  = view.findViewById(R.id.tvFirstName);
            mLastName   = view.findViewById(R.id.tvLastName);
            mEmail      = view.findViewById(R.id.tvEmail);
            mPhone      = view.findViewById(R.id.tvPhone);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact vContact = mListContact.get(position);

        holder.mFirstName.setText( vContact.getFirstName() );
        holder.mLastName.setText( vContact.getLastName() );
        holder.mEmail.setText( vContact.getEmail() );
        holder.mPhone.setText( vContact.getPhone() );

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.addAndEditContact(true, vContact, position);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mListContact.size();
    }


}
