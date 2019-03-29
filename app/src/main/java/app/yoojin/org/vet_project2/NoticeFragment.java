package app.yoojin.org.vet_project2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NoticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoticeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<NoticeVO> data;
    private NoticeDataAdapter adapter;
    private Toolbar topToolbar;
    private SearchView searchView;

    private SwipeRefreshLayout swipeRefreshLayout;


    public NoticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoticeFragment.
     */

    /* newInstance 안에서 Fragment를 생성하고 intent를 통해 데이터를 넘겨준다
     * 이렇게 전달된 bundle 데이터는 onCreate에서 getArguments(). 이후에 있는 getString, getInt로 받을 수 있다
    */
    public static NoticeFragment newInstance(String param1, String param2) {
        NoticeFragment fragment = new NoticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice,null);
        recyclerView = view.findViewById(R.id.recyclerNotice);

        initViews();    // 리사이클러뷰

        // 리사이클러뷰 밑으로 당겨서 새로고침
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(swipeListener);


        return view;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/


    // 리사이클러뷰
    private void initViews(){

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        loadReview();
    }


    // 공지 리스트 로드
    private void loadReview(){
        Call<List<NoticeVO>> call = RetrofitInit.getInstance().getService().getNoticeList();

        call.enqueue(new Callback<List<NoticeVO>>() {
            @Override
            public void onResponse(Call<List<NoticeVO>> call, Response<List<NoticeVO>> response) {
                data = response.body();
                adapter = new NoticeDataAdapter(data);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<NoticeVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

    // SwipeRefreshLayout (당겨서 새로고침) 리스너
    SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            // 새로고침 코드
            initViews();
            // 새로고침 완료
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    /*// Top Navigation에 top_navigation.xml을 집어넣는다
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation, menu);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        // 검색 버튼 클릭 시 searchView 꽉차게
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            // 검색 버튼 클릭 시 searchView에 힌트 추가
            searchView.setQueryHint("결과 내 검색");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String query) {
                    adapter.filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.filter(query);
                    return false;
                }
            });
        }

        return true;
    }*/

}
