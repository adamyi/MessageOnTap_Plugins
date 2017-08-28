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

    public ScriptAdapter(List<Script> mScriptList, Toolbar toolbar) {
        this.mScriptList = mScriptList;
        this.mToolbar = toolbar;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        View mContactView;
        ImageView mContactImage;
        TextView mContactName;
        LinearLayout mContactLayout;
        CheckBox mContactCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mContactView = itemView;
            mContactLayout =  itemView.findViewById(R.id.linearLayout);
            mContactImage =  itemView.findViewById(R.id.contact_image);
            mContactName = itemView.findViewById(R.id.contact_name);
            mContactCheckBox = itemView.findViewById(R.id.contact_checkbox);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Script script = mScriptList.get(position);
                if (!script.isFlag()){
                    Script.setAllFlags(false);
                    script.setFlag(true);
                }else {
                    Script.setAllFlags(false);
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
        holder.mContactImage.setImageDrawable(script.getContactPicture());
        holder.mContactName.setText(script.getName());
        setSelection(holder, script);
    }

    @Override
    public int getItemCount() {
        return mScriptList.size();
    }
    private static void setSelection(ViewHolder holder, Script script){
        if (script.isFlag()){
            holder.mContactCheckBox.setChecked(true);
        }else {
            holder.mContactCheckBox.setChecked(false);
        }
    }

    private static void setAllSelectionByBoolean(Boolean selection, RecyclerView recyclerView){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            holder.mContactCheckBox.setChecked(selection);
        }
    }
    
    public static void setAllSelection(RecyclerView recyclerView){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            holder.mContactCheckBox.setChecked(false);
        }
        if (Script.scriptList.size() == 0){
            setAllSelectionByBoolean(false,recyclerView);
        } else{
            for (Script script : Script.scriptList) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (script.getName().equals(holder.mContactName.getText())){
                        if (script.isFlag()){
                            holder.mContactCheckBox.setChecked(true);
                        } else {
                            holder.mContactCheckBox.setChecked(false);
                        }
                        Log.i("Script", "SetAllSavedSelection: "+holder.mContactName.getText() + script.isFlag());
                    }
                }
            }
            Log.i("Script", "SetAllSelection:  completed");
        }

    }

}
