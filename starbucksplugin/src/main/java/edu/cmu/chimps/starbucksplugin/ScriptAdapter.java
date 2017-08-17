package edu.cmu.chimps.starbucksplugin;

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


public class ScriptAdapter extends RecyclerView.Adapter<ScriptAdapter.ViewHolder> {
    private List<Script> mScriptList;
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
            contactLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            contactImage = (ImageView) itemView.findViewById(R.id.contact_image);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            contactCheckBox = (CheckBox) itemView.findViewById(R.id.contact_checkbox);
        }
    }

    public ScriptAdapter(List<Script> mScriptList, Toolbar toolbar) {
        this.mScriptList = mScriptList;
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
                Script script = mScriptList.get(position);
                if (!script.isFlag()){
                    Script.SetAllFlag(false);
                    script.setFlag(true);
                }else {
                    Script.SetAllFlag(false);
                }
                StarbucksSettingActivity.listener.onChange(true);
                //Toast.makeText(view.getContext(), "click " + "position:"+position, Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Script script = mScriptList.get(position);
        holder.contactImage.setImageDrawable(script.getContactPicture());
        holder.contactName.setText(script.getName());
        SetSelection(holder, script);
    }

    @Override
    public int getItemCount() {
        return mScriptList.size();
    }
    public  static void SetSelection(ViewHolder holder, Script script){
        if (script.isFlag()){
            holder.contactCheckBox.setChecked(true);
        }else {
            holder.contactCheckBox.setChecked(false);
        }
    }

    public static void SetAllSelectionByBoolean(Boolean selection, RecyclerView recyclerView){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            holder.contactCheckBox.setChecked(selection);
        }
    }
    
    public static void SetAllSelection(RecyclerView recyclerView){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            holder.contactCheckBox.setChecked(false);
        }
        if (Script.scriptList.size() == 0){
            SetAllSelectionByBoolean(false,recyclerView);
        } else{
            for (Script script : Script.scriptList) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (script.getName().equals(holder.contactName.getText())){
                        if (script.isFlag()){
                            holder.contactCheckBox.setChecked(true);
                        } else {
                            holder.contactCheckBox.setChecked(false);
                        }
                        Log.i("Script", "SetAllSavedSelection: "+holder.contactName.getText() + script.isFlag());
                    }
                }
            }
            Log.i("Script", "SetAllSelection:  completed");
        }

    }

}
