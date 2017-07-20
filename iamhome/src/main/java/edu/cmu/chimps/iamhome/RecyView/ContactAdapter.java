package edu.cmu.chimps.iamhome.RecyView;

import android.app.Activity;
import android.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.cmu.chimps.iamhome.R;

/**
 * Created by knight006 on 7/18/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> mContactList;
    private Activity mActivity;
    private ActionMode mActionmode;

    //新建viewholder类
    static class ViewHolder extends RecyclerView.ViewHolder{
        View contactView;
        ImageView contactImage;
        TextView contactName;
        LinearLayout contactLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            contactView = itemView;
            contactLayout = itemView.findViewById(R.id.linearLayout);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
        }
    }
    //创建 ActionMode.Callback实例
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.actionmode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };


    public ContactAdapter(List<Contact> mContactList, Activity activity) {
        this.mContactList = mContactList;
        this.mActivity = activity;
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
                    //mActionmode =  mActivity.startActionMode(mActionModeCallback);
                    toggleSelection(holder, contact);
                    String title = " " + SelectedItemCount() + "selected";
                    mActivity.setTitle(title);
                } else {
                    toggleSelection(holder, contact);
                    String title = " " + SelectedItemCount() + " selected";
                    mActivity.setTitle(title);
                }
                Toast.makeText(view.getContext(), "click " + "position:"+position, Toast.LENGTH_SHORT).show();

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.contactImage.setImageDrawable(contact.getContactPicture());
        holder.contactName.setText(contact.getName());
        if (contact.isFlag()){
            holder.contactLayout.setSelected(true);
        }else {
            holder.contactLayout.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public int SelectedItemCount(){
        int count = 0;
        for (int i=0; i<mContactList.size(); i++){
            if (mContactList.get(i).isFlag()){
                count++;
            }
        }
        return count;
    }

    public void toggleSelection(ViewHolder holder, Contact contact){
        if (contact.isFlag()){
            contact.setFlag(false);
            holder.contactLayout.setSelected(false);
        } else {
            contact.setFlag(true);
            holder.contactLayout.setSelected(true);
        }
    }
}
