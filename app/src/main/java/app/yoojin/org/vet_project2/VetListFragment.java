package app.yoojin.org.vet_project2;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VetListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VetListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // 받아올 데이터들
    private Double lat, lng;
    private String searchKeyword, nowP, nowC, province, city;


    private RecyclerView recyclerView;
    private List<VetVO> data;
    private VetDataAdapter adapter;
    private TextView textView, total;
    private Toolbar topToolbar;
    private Spinner sortSpinner;
    private String selectItem;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public VetListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VetListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VetListFragment newInstance(String param1, String param2) {
        VetListFragment fragment = new VetListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Intent 값 받아오기
        if (getArguments() != null) {
            lat = getArguments().getDouble("lat");
            lng = getArguments().getDouble("lng");
            searchKeyword = getArguments().getString("searchKeyword");
            nowP = getArguments().getString("nowP");
            nowC = getArguments().getString("nowC");
            province = getArguments().getString("province");
            city = getArguments().getString("city");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vet_list, null);

        // 정렬 스피너
        sortSpinner = view.findViewById(R.id.sortSpinner);

        textView = view.findViewById(R.id.textView);
        recyclerView = view.findViewById(R.id.recycler);
        total = view.findViewById(R.id.total);

        initViews();
        if(searchKeyword != null){
            textView.setText("'"+searchKeyword+"'");
        } else if(lat != 0 && lng != 0){
            //textView.setText("가까운 동물 병원");
            textView.setText("'"+nowP +" "+ nowC+"'");
        }
        else{
            textView.setText("'"+province +" "+ city+"'");
        }

        // 리사이클러뷰 밑으로 당겨서 새로고침
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(swipeListener);

        // Inflate the layout for this fragment
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
        //loadJSON();

        if(searchKeyword!=null) {
            searchByName();
        }  else if(lat != 0 && lng != 0){
            searchNearest();
        }
        else{
            searchByRegion();
        }
    }

    // 상호명으로 검색
    private void searchByName(){
        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByName(searchKeyword);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, final Response<List<VetVO>> response) {

                data = response.body();

                sortSpinner.setOnItemSelectedListener(spinnerListener); // 정렬 스피너

                adapter = new VetDataAdapter(data);
                recyclerView.setAdapter(adapter);

                String totalRes = String.format("%d", adapter.getItemCount()-1);
                total.setText(totalRes+"건");
            }

            @Override
            public void onFailure(Call<List<VetVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

    // 지역으로 검색
    private void searchByRegion(){
        HashMap<String, String> region = new HashMap<>();
        region.put("province", province);
        region.put("city", city);

        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByRegion(region);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, Response<List<VetVO>> response) {
                data = response.body();

                sortSpinner.setOnItemSelectedListener(spinnerListener);

                adapter = new VetDataAdapter(data);
                recyclerView.setAdapter(adapter);

                String totalRes = String.format("%d", adapter.getItemCount()-1);
                total.setText(totalRes+"건");
            }

            @Override
            public void onFailure(Call<List<VetVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

    // 가까운 병원 검색 - 현재 시도, 시군구로 검색하는 걸로 임시
    private void searchNearest(){
        HashMap<String, String> region = new HashMap<>();
        region.put("province", nowP);
        region.put("city", nowC);

        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByRegion(region);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, final Response<List<VetVO>> response) {
                data = response.body();
                adapter = new VetDataAdapter(data);
                adapter.notifyDataSetChanged();

                sortSpinner.setOnItemSelectedListener(spinnerListener);

                Log.d("스피너","안호출");
                recyclerView.setAdapter(adapter);

                String totalRes = String.format("%d", adapter.getItemCount()-1);
                total.setText(totalRes+"건");
            }

            @Override
            public void onFailure(Call<List<VetVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

    // 정렬 스피너 리스너
    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectItem = parent.getItemAtPosition(position).toString(); //상호명 or 지역
            Log.d("선택",selectItem);
            switch (selectItem){
                case "상호명 오름차순":
                    Log.d("상호명","오름차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.NameAscCompare nasc = new SortWithSpinner.NameAscCompare();
                    nasc.compare(data.get(0), data.get(1));
                    Collections.sort(data, nasc);
                    break;
                case "상호명 내림차순":
                    Log.d("상호명","내림차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.NameDescCompare ndesc = new SortWithSpinner.NameDescCompare();
                    ndesc.compare(data.get(0), data.get(1));
                    Collections.sort(data, ndesc);
                    break;
                case "지역명 오름차순":
                    Log.d("지역명","오름차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.RegionAscCompare rasc = new SortWithSpinner.RegionAscCompare();
                    rasc.compare(data.get(0), data.get(1));
                    Collections.sort(data, rasc);
                    break;
                case "지역명 내림차순":
                    Log.d("지역명","내림차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.RegionDescCompare rdesc = new SortWithSpinner.RegionDescCompare();
                    rdesc.compare(data.get(0), data.get(1));
                    Collections.sort(data, rdesc);
                    break;
                case "평점 높은 순":
                    Log.d("평점","높은 순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.RateCompare rate = new SortWithSpinner.RateCompare();
                    rate.compare(data.get(0), data.get(1));
                    Collections.sort(data, rate);
                    break;
                case "후기 많은 순":
                    Log.d("후기","많은 순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.ReviewCompare review = new SortWithSpinner.ReviewCompare();
                    review.compare(data.get(0), data.get(1));
                    Collections.sort(data, review);
                    break;
                case "조회 많은 순":
                    Log.d("조회","많은 순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.ViewCompare viewCompare = new SortWithSpinner.ViewCompare();
                    viewCompare.compare(data.get(0), data.get(1));
                    Collections.sort(data, viewCompare);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d("낫띵","셀렉티드");
        }
    };

    // SwipeRefreshLayout (당겨서 새로고침) 리스너
    SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            // 새로고침 코드
            initViews();
            sortSpinner.setSelection(0);
            // 새로고침 완료
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    /*SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(swipeListener);*/

    /*// Top Navigation에 top_navigation.xml을 집어넣는다 + 서치뷰 검색
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
                    //adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    //adapter.getFilter().filter(query);
                    adapter.filter(query);
                    return false;
                }
            });
        }

        return true;
    }*/

}
