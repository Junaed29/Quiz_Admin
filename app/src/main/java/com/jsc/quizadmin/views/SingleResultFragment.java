package com.jsc.quizadmin.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.adapters.ResultTableAdapter;
import com.jsc.quizadmin.model.CollectionNameModel;
import com.jsc.quizadmin.model.StudentResultModel;
import com.jsc.quizadmin.pdf.Pdf_creator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SingleResultFragment extends Fragment {

    private static final String TAG = "SingleResultFragment";

    Button pdfButton;

    ResultTableAdapter tableAdapter;

    TextView idTextView;
    TextView totalQuestionTextView;
    TextView correctTextView;
    TextView wrongTextView;
    TextView timeOutTextView;

    RecyclerView recyclerView;

    String correctAnswerNumber = "", totalQuestionNumber = "", wrongAnswerNumber = "", timeOutNumber = "";
    String studentId = "";

    String categoryDocumentId, resultsDocumentId, quizTitle, courseId;

    ArrayList<String> questionArrayList;
    ArrayList<String> actualAnswerArrayList;
    ArrayList<String> selectedAnswerArrayList;

    ArrayList<String> questionFieldNameArrayList;
    ArrayList<String> actualAnswerFieldNameArrayList;
    ArrayList<String> selectedAnswerFieldNameArrayList;

    private ProgressBar progressBar;

    StudentResultModel resultModel;

    Pdf_creator pdf_creator;

    public SingleResultFragment() {
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
        return inflater.inflate(R.layout.fragment_single_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdf_creator = new Pdf_creator(getContext());

        categoryDocumentId = SingleResultFragmentArgs.fromBundle(getArguments()).getCategoryDocumentId();
        resultsDocumentId = SingleResultFragmentArgs.fromBundle(getArguments()).getResultDocumentId();
        quizTitle = SingleResultFragmentArgs.fromBundle(getArguments()).getQuizTitle();
        courseId = SingleResultFragmentArgs.fromBundle(getArguments()).getCourseId();

        tableAdapter = new ResultTableAdapter(getContext());

        idTextView = view.findViewById(R.id.idTextViewInTable);
        totalQuestionTextView = view.findViewById(R.id.totalQuestionTextViewInTable);
        correctTextView = view.findViewById(R.id.totalCorrectTextViewInTable);
        wrongTextView = view.findViewById(R.id.totalWrongTextViewInTable);
        timeOutTextView = view.findViewById(R.id.totalTimeOutTextViewInTable);
        pdfButton = view.findViewById(R.id.tableDownloadButton);
        recyclerView = view.findViewById(R.id.tableRecyclerView);
        progressBar = view.findViewById(R.id.progressBar2);

        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionArrayList.size() > 0) {
                    pdf_creator.permissionForSingleResult(resultModel.getStudentId(), quizTitle, courseId, resultModel, questionArrayList, actualAnswerArrayList, selectedAnswerArrayList);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        //tableView = view.findViewById(R.id.tableId);

        FirebaseFirestore.getInstance()
                .collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                .document(categoryDocumentId)
                .collection(CollectionNameModel.Question_RESULTS_NAME)
                .document(resultsDocumentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                questionArrayList = new ArrayList<>();
                                actualAnswerArrayList = new ArrayList<>();
                                selectedAnswerArrayList = new ArrayList<>();

                                questionFieldNameArrayList = new ArrayList<>();
                                actualAnswerFieldNameArrayList = new ArrayList<>();
                                selectedAnswerFieldNameArrayList = new ArrayList<>();


                                Map<String, Object> map = document.getData();
                                if (map != null) {
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        String filedName = entry.getKey().toString();
                                        if (filedName.contains("Question number")) {
                                            questionFieldNameArrayList.add(filedName);
                                        } else if (filedName.contains("Correct Answer")) {
                                            actualAnswerFieldNameArrayList.add(filedName);
                                        } else if (filedName.contains("Selected Answer")) {
                                            selectedAnswerFieldNameArrayList.add(filedName);
                                        }
                                    }
                                }

                                Collections.sort(questionFieldNameArrayList);
                                Collections.sort(actualAnswerFieldNameArrayList);
                                Collections.sort(selectedAnswerFieldNameArrayList);

                                //So what you need to do with your list
                                for (int i = 0; i < questionFieldNameArrayList.size(); i++) {
                                    String question = (String) document.get(questionFieldNameArrayList.get(i));
                                    questionArrayList.add(question);
                                    String actual = (String) document.get(actualAnswerFieldNameArrayList.get(i));
                                    actualAnswerArrayList.add(actual);
                                    String selected = (String) document.get(selectedAnswerFieldNameArrayList.get(i));
                                    selectedAnswerArrayList.add(selected);
                                }

                                resultModel = document.toObject(StudentResultModel.class);

                                totalQuestionNumber = "Total Question : " + resultModel.getTotalQuestion() + "";
                                correctAnswerNumber = "Correct : " + resultModel.getCorrect() + "";
                                wrongAnswerNumber = "Wrong : " + resultModel.getWrong() + "";
                                timeOutNumber = "TimeOut : " + resultModel.getUnanswered() + "";
                                studentId = resultModel.getStudentId() + "";

                                setVies();

                            }
                        }
                    }
                });
    }

    void setVies() {
        totalQuestionTextView.setText(totalQuestionNumber);
        correctTextView.setText(correctAnswerNumber);
        wrongTextView.setText(wrongAnswerNumber);
        timeOutTextView.setText(timeOutNumber);
        idTextView.setText(studentId);


        tableAdapter.setAllArrayList(questionArrayList, actualAnswerArrayList, selectedAnswerArrayList);

        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(tableAdapter);
    }
}