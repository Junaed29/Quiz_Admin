package com.jsc.quizadmin.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.adapters.AllResultAdapter;
import com.jsc.quizadmin.model.CollectionNameModel;
import com.jsc.quizadmin.model.StudentResultModel;
import com.jsc.quizadmin.pdf.Pdf_creator;

import java.util.ArrayList;
import java.util.List;


public class ResultsFragment extends Fragment implements View.OnClickListener, AllResultAdapter.ResultListener {

    private static final String TAG = "ResultsFragment";

    String categoryDocumentId = "";
    String quizTitle = "", courseId, batch, dept;

    NavController controller;

    RecyclerView recyclerView;
    ImageButton pdfImageButton;
    TextView pdfTextView;
    ProgressBar progressBar;

    ArrayList<String> idArrayList;
    ArrayList<String> correctArrayList;
    ArrayList<String> totalQuestionArrayList;
    ArrayList<String> percentageArrayList;
    AllResultAdapter resultAdapter;

    List<StudentResultModel> resultModelList;

    public ResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        categoryDocumentId = ResultsFragmentArgs.fromBundle(getArguments()).getCategoryDocumentId();
        quizTitle = ResultsFragmentArgs.fromBundle(getArguments()).getQuizTitle();
        courseId = ResultsFragmentArgs.fromBundle(getArguments()).getCourseId();
        batch = ResultsFragmentArgs.fromBundle(getArguments()).getBatch();
        dept = ResultsFragmentArgs.fromBundle(getArguments()).getDept();

        recyclerView = view.findViewById(R.id.question_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        resultAdapter = new AllResultAdapter(this);


        pdfImageButton = view.findViewById(R.id.question_fabId);
        pdfTextView = view.findViewById(R.id.download_title);
        progressBar = view.findViewById(R.id.question_progressId);

        FirebaseFirestore.getInstance()
                .collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                .document(categoryDocumentId)
                .collection(CollectionNameModel.Question_RESULTS_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            idArrayList = new ArrayList<>();
                            correctArrayList = new ArrayList<>();
                            totalQuestionArrayList = new ArrayList<>();
                            percentageArrayList = new ArrayList<>();

                            resultModelList = task.getResult().toObjects(StudentResultModel.class);

                            for (StudentResultModel resultModel : resultModelList) {
                                idArrayList.add(resultModel.getStudentId());
                                correctArrayList.add(resultModel.getCorrect().toString());
                                totalQuestionArrayList.add(resultModel.getTotalQuestion());
                                percentageArrayList.add(getPercentage(resultModel.getCorrect(), resultModel.getWrong(), resultModel.getUnanswered()));
                            }

                            resultAdapter.setResultModelList(resultModelList);

                            progressBar.setVisibility(View.GONE);

                            recyclerView.setAdapter(resultAdapter);
                        }
                    }
                });

        pdfTextView.setOnClickListener(this);
        pdfImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Pdf_creator pdf_creator = new Pdf_creator(getContext());
        //pdf_creator.permissionForShowingCodes("Student_Results", idArrayList, totalQuestionArrayList);
        pdf_creator.permissionForResultDistribution("Student_Results", quizTitle, courseId, batch, dept, idArrayList, totalQuestionArrayList, correctArrayList, percentageArrayList);
    }

    String getPercentage(Long correct, Long wrong, Long unanswered) {
        long total = correct + wrong + unanswered;

        long percent = (correct * 100) / total;

        return percent + "%";
    }

    @Override
    public void onItemClick(int position) {
        StudentResultModel resultModel = resultModelList.get(position);

        String resultsDocumentId = resultModel.getDocumentId();

        ResultsFragmentDirections.ActionResultsFragmentToSingleResultFragment action = ResultsFragmentDirections.actionResultsFragmentToSingleResultFragment();
        action.setCategoryDocumentId(categoryDocumentId);
        action.setResultDocumentId(resultsDocumentId);
        action.setQuizTitle(quizTitle);
        action.setCourseId(courseId);

        controller.navigate(action);
    }
}