package edu.cmu.chimps.googledocsplugin;

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

import static edu.cmu.chimps.googledocsplugin.Contact.selectedItemCount;
import static edu.cmu.chimps.googledocsplugin.Contact.contactList;
import static edu.cmu.chimps.googledocsplugin.Contact.toggleFlag;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> mContactList;
    protected Toolbar mToolbar;

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
            contactImage =  itemView.findViewById(R.id.contact_image);
            contactName =  itemView.findViewById(R.id.contact_name);
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
                if (selectedItemCount()==contactList.size()){
                    GoogleDocsSettingsActivity.iconChangeListener.onChange(false);
                }
                Contact contact = mContactList.get(position);
                toggleFlag(contact);
                String title = " " + selectedItemCount() + " selected";
                mToolbar.setSubtitle(title);
                if (selectedItemCount()== contactList.size()){
                    Log.e("Test", "Listener sent");
                    GoogleDocsSettingsActivity.iconChangeListener.onChange(true);
                }


                setSelection(holder, contact);
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
        setSelection(holder, contact);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }


    public  static void setSelection(ViewHolder holder, Contact contact){
        if (contact.isFlag()){
            holder.contactLayout.setSelected(true);
            holder.contactCheckBox.setChecked(true);
        }else {
            holder.contactLayout.setSelected(false);
            holder.contactCheckBox.setChecked(false);
        }
    }

    public static void setAllSelections(Boolean selection, RecyclerView recyclerView){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            Contact.setAllFlags(selection);
            holder.contactLayout.setSelected(selection);
            holder.contactCheckBox.setChecked(selection);
        }
    }
    
    public static void setAllSavedSelections(RecyclerView recyclerView){
        Set<String> set = ContactStorage.getContacts(GoogleDocApplication.getAppContext(), ContactStorage.KEY_ALL_SELECT_STORAGE);
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            holder.contactLayout.setSelected(false);
            holder.contactCheckBox.setChecked(false);
        }
        if (set.isEmpty()){
            setAllSelections(false,recyclerView);
        } else{
            for (String str: set) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (str.equals(holder.contactName.getText())){
                        holder.contactLayout.setSelected(true);
                        holder.contactCheckBox.setChecked(true);
                    }
                }
            }
        }

    }

}
