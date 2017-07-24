package edu.cmu.chimps.iamhome.RecyView;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import edu.cmu.chimps.iamhome.MyApplication;
import edu.cmu.chimps.iamhome.R;
import edu.cmu.chimps.iamhome.SharedPrefs.ContactStorage;

import static edu.cmu.chimps.iamhome.RecyView.Contact.SelectedItemCount;
import static edu.cmu.chimps.iamhome.RecyView.Contact.toggleFlag;

/**
 * Created by knight006 on 7/18/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> mContactList;
    private Activity mActivity;
    private Toolbar mToolbar;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View contactView;
        ImageView contactImage;
        TextView contactName;
        LinearLayout contactLayout;
        CheckBox contactCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            contactView = itemView;
            contactLayout = itemView.findViewById(R.id.linearLayout);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
            contactCheckBox = itemView.findViewById(R.id.contact_checkbox);
        }
    }


    public ContactAdapter(List<Contact> mContactList, Toolbar toolbar) {
        this.mContactList = mContactList;
        this.mToolbar = toolbar;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Contact contact = mContactList.get(position);
                if (SelectedItemCount()==0){
                    toggleFlag(contact);
                    String title = " " + SelectedItemCount() + " selected";
                    mToolbar.setSubtitle(title);
                } else {
                    toggleFlag(contact);
                    String title = " " + SelectedItemCount() + " selected";
                    mToolbar.setSubtitle(title);
                }
                SetSelection(holder, contact);



                //Toast.makeText(view.getContext(), "click " + "position:"+position, Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.contactImage.setImageDrawable(contact.getContactPicture());
        holder.contactName.setText(contact.getName());
        SetSelection(holder, contact);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }


    public  void SetSelection(ViewHolder holder, Contact contact){
        if (contact.isFlag()){
            holder.contactLayout.setSelected(true);
            holder.contactCheckBox.setChecked(true);
        }else {
            holder.contactLayout.setSelected(false);
            holder.contactCheckBox.setChecked(false);
        }
    }

    public static void SetAllSelection(Boolean selection, RecyclerView recyclerView){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            Contact.SetAllFlag(selection);
            holder.contactLayout.setSelected(selection);
            holder.contactCheckBox.setChecked(selection);
            Log.i("iiii", "SetAllSelection: ");
        }
    }
    
    public static void SetAllSavedSelection(RecyclerView recyclerView){
        Set<String> set = ContactStorage.getContacts(MyApplication.getContext(), ContactStorage.ALLSELECTSTORAGE);
        Log.i("iiii", "SetAllSavedSelection: enter");
        if (set.size() == 0){
            SetAllSelection(false,recyclerView);
        } else{
            for (String str: set) {
                Log.i("iiii", "SetAllSavedSelection:111 ");
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    Log.i("iiii", "SetAllSavedSelection: "+holder.contactName.getText());
                    Contact.SetAllFlag(false);
                    holder.contactLayout.setSelected(false);
                    holder.contactCheckBox.setChecked(false);
                    if (str == holder.contactName.getText()){
                        Contact.SetAllFlag(true);
                        holder.contactLayout.setSelected(true);
                        holder.contactCheckBox.setChecked(true);
                    }
                    Log.i("iiii", "SetAllSelection:  completed");
                }
            }
        }

    }

}
