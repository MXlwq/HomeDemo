package com.example.mi.coordinatorlayoutdemo;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    private RecyclerView recyclerView;
    private ImageView topsite;
    private LinearLayout mTextView;
    private ImageView mImageView;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.rv);
        topsite=root.findViewById(R.id.topsite);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_simple, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ViewHolder vh = (ViewHolder) holder;
                vh.text.setText("Fake Item " + (position + 1));
                vh.text2.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
            }

            @Override
            public int getItemCount() {
                return 20;
            }

            class ViewHolder extends RecyclerView.ViewHolder {

                TextView text;
                TextView text2;

                public ViewHolder(View itemView) {
                    super(itemView);

                    text = (TextView) itemView.findViewById(R.id.text);
                    text2 = (TextView) itemView.findViewById(R.id.text2);
                }

            }
        });
//        mTextView = (LinearLayout) root.findViewById(R.id.edit_search);
//        mImageView = (ImageView) root.findViewById(R.id.scrolling_header);
//        mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mImageView.setTranslationY(-300);
//            }
////        });
        return root;
    }

    public void moveEditTextPosition(float positionOffset) {
//        mImageView.setTranslationY(-300 * positionOffset);
    }

    public void resetRV() {
        recyclerView.setTranslationY(420);
        recyclerView.smoothScrollToPosition(0);
        topsite.setTranslationY(0);
        ScrollOrietationUtils.getInstance().setCanPullDown(true);
    }
}
