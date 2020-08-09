package com.jsc.quizadmin.views;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.adapters.AllQuestionsAdapter;
import com.jsc.quizadmin.model.CollectionNameModel;
import com.jsc.quizadmin.model.QuestionModel;
import com.shawnlin.numberpicker.NumberPicker;

import es.dmoral.toasty.Toasty;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AllQuestionListFragment extends Fragment implements AllQuestionsAdapter.QuestionListener, View.OnClickListener {
    private static final String TAG = "AllQuestionListFragment";
    RecyclerView questionRecyclerView;
    ProgressBar questionProgressBar;
    FloatingActionButton questionFab;

    Button showResultButton;
    Dialog updateDialog;

    Dialog previewDialog;

    TextView mainPageTitle;
    TextView mainPageDesc;

    AllQuestionsAdapter questionsAdapter;

    NavController navController;

    String categoryDocumentId, quizTitle, courseId, batch, dept;

    String question = "", answer = "", otherOptionOne = "", otherOptionTwo = "", otherOptionThree = "";
    long timer;

    String option_a = "", option_b = "", option_c = "";

    public AllQuestionListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_question_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        showResultButton = view.findViewById(R.id.results_btn);


        categoryDocumentId = AllQuestionListFragmentArgs.fromBundle(getArguments()).getCategoryDocumentId();
        quizTitle = AllQuestionListFragmentArgs.fromBundle(getArguments()).getQuizTitle();
        courseId = AllQuestionListFragmentArgs.fromBundle(getArguments()).getCourseId();
        batch = AllQuestionListFragmentArgs.fromBundle(getArguments()).getBatch();
        dept = AllQuestionListFragmentArgs.fromBundle(getArguments()).getDept();
        String desc = courseId + "\n" + dept + "  " + batch;


        //Hide Results Button
        FirebaseFirestore.getInstance()
                .collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                .document(categoryDocumentId)
                .collection(CollectionNameModel.Question_RESULTS_NAME)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.size()>0){
                            showResultButton.setVisibility(View.VISIBLE);
                        }else {
                            showResultButton.setVisibility(View.GONE);
                        }
                    }
                });

        mainPageTitle = view.findViewById(R.id.question_page_title);
        mainPageTitle.setText(quizTitle);

        mainPageDesc = view.findViewById(R.id.question_batch_title);
        mainPageDesc.setText(desc);


        questionRecyclerView = view.findViewById(R.id.question_recycler_view);
        questionProgressBar = view.findViewById(R.id.question_progressId);
        questionFab = view.findViewById(R.id.question_fabId);

        questionFab.setOnClickListener(this);
        showResultButton.setOnClickListener(this);

        setQuestionRecyclerView();
    }

    public void setQuestionRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                .document(categoryDocumentId)
                .collection(CollectionNameModel.Question_COLLECTION_NAME);

        FirestoreRecyclerOptions<QuestionModel> options = new FirestoreRecyclerOptions.Builder<QuestionModel>()
                .setQuery(query, QuestionModel.class)
                .build();

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                questionProgressBar.setVisibility(View.GONE);
            }
        });

        questionsAdapter = new AllQuestionsAdapter(options, this);

        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionRecyclerView.setHasFixedSize(true);

        questionRecyclerView.setAdapter(questionsAdapter);

        questionsAdapter.startListening();
    }


    @Override
    public void questionDeleteClicked(DocumentSnapshot snapshot) {
        QuestionModel model = snapshot.toObject(QuestionModel.class);
        setDeleteQuestionDialog(model);
    }

    @Override
    public void questionEditClicked(DocumentSnapshot snapshot) {
        QuestionModel model = snapshot.toObject(QuestionModel.class);
        questionEditDialog(model);
    }

    private void setDeleteQuestionDialog(QuestionModel questionModel) {
        final String questionDocumentId = questionModel.getQuestionId();

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.category_delete_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        dialog.setCanceledOnTouchOutside(false);

        TextView titleTextView = dialog.findViewById(R.id.delete_dialog_titleId);
        TextView messageTextView = dialog.findViewById(R.id.delete_dialog_messageId);
        String message = "Are you sure to delete this Question permanently ?\nAll The Other Options of this question will be deleted also.";
        String title = "Delete This Question ??";
        titleTextView.setText(title);
        messageTextView.setText(message);

        Button cancelButton = dialog.findViewById(R.id.cancel_btn);
        Button confirmButton = dialog.findViewById(R.id.confirm_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Confirm", Toast.LENGTH_SHORT).show();
                FirebaseFirestore.getInstance().collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                        .document(categoryDocumentId)
                        .collection(CollectionNameModel.Question_COLLECTION_NAME)
                        .document(questionDocumentId)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(getContext(), "Deleted Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(getContext(), "Something Wrong", Toast.LENGTH_LONG).show();
                                }
                                dialog.dismiss();
                            }
                        });
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }


    private void questionEditDialog(final QuestionModel model) {

        question = model.getQuestion();
        answer = model.getAnswer();
        otherOptionOne = model.getOption_a();
        otherOptionTwo = model.getOption_b();
        otherOptionThree = model.getOption_c();
        timer = model.getTimer();


        updateDialog = new Dialog(getActivity());
        updateDialog.setContentView(R.layout.question_edit_dialog);
        updateDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        updateDialog.setCanceledOnTouchOutside(false);


        final NumberPicker numberPicker = updateDialog.findViewById(R.id.numberPickerId);

        numberPicker.setValue((int) timer);

        final EditText questionEditText = updateDialog.findViewById(R.id.questionEditText);
        questionEditText.setText(question);

        final EditText answerEditText = updateDialog.findViewById(R.id.answerEditText);
        answerEditText.setText(answer);

        final EditText otherOption1EditText = updateDialog.findViewById(R.id.otherOption_1EditText);
        otherOption1EditText.setText(otherOptionOne);


        final EditText otherOption2EditText = updateDialog.findViewById(R.id.otherOption_2EditText);
        final EditText otherOption3EditText = updateDialog.findViewById(R.id.otherOption_3EditText);

        ImageButton addImageButton = updateDialog.findViewById(R.id.addButton);
        ImageButton removeImageButton = updateDialog.findViewById(R.id.removeButton);

        Button cancelButton = updateDialog.findViewById(R.id.question_edit_cancel_button);
        Button updateButton = updateDialog.findViewById(R.id.question_edit_update_button);


        final LinearLayout otherOption2LinearLayout = updateDialog.findViewById(R.id.otherOption2LinearLayout);
        final LinearLayout otherOption3LinearLayout = updateDialog.findViewById(R.id.otherOption3LinearLayout);

        if (!otherOptionTwo.equals("null")) {
            otherOption2LinearLayout.setVisibility(View.VISIBLE);
            otherOption2EditText.setText(otherOptionTwo);
        }

        if (!otherOptionThree.equals("null")) {
            otherOption3LinearLayout.setVisibility(View.VISIBLE);
            otherOption3EditText.setText(otherOptionThree);
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question = questionEditText.getText().toString();
                question = question.trim();

                answer = answerEditText.getText().toString();
                answer = answer.trim();

                otherOptionOne = otherOption1EditText.getText().toString();
                otherOptionOne = otherOptionOne.trim();


                otherOptionTwo = otherOption2EditText.getText().toString();
                otherOptionTwo = otherOptionTwo.trim();

                otherOptionThree = otherOption3EditText.getText().toString();
                otherOptionThree = otherOptionThree.trim();

                timer = numberPicker.getValue();

                if (question.isEmpty()) {
                    Toasty.error(getContext(), "Question is required", Toast.LENGTH_SHORT).show();
                } else if (answer.isEmpty()) {
                    Toasty.error(getContext(), "Answer is required", Toast.LENGTH_SHORT).show();
                } else if (otherOptionOne.isEmpty()) {
                    Toasty.error(getContext(), "At least One Other option needed", Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.warning(getContext(), "Option will be shuffle in real QUIZ", Toast.LENGTH_LONG).show();
                    setPreviewDialog(model.getQuestionId());
                }
            }
        });

        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherOption3LinearLayout.getVisibility() == View.VISIBLE) {
                    otherOption3LinearLayout.setVisibility(View.GONE);
                    otherOptionThree = "";
                    otherOption3EditText.setText("");
                } else if (otherOption2LinearLayout.getVisibility() == View.VISIBLE) {
                    otherOption2LinearLayout.setVisibility(View.GONE);
                    otherOptionTwo = "";
                    otherOption2EditText.setText("");
                } else {
                    Toasty.warning(getContext(), "Minimum 2 Options", Toast.LENGTH_SHORT).show();
                }
            }
        });


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(otherOption2LinearLayout.getVisibility() == View.VISIBLE)) {
                    otherOption2LinearLayout.setVisibility(View.VISIBLE);
                    otherOptionTwo = "";
                    otherOption2EditText.setText("");
                } else if (!(otherOption3LinearLayout.getVisibility() == View.VISIBLE)) {
                    otherOption3LinearLayout.setVisibility(View.VISIBLE);
                    otherOptionThree = "";
                    otherOption3EditText.setText("");
                } else {
                    Toasty.warning(getContext(), "Maximum 4 Options", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });


        updateDialog.show();
        Window window = updateDialog.getWindow();
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.question_fabId){
            AllQuestionListFragmentDirections.ActionAllQuestionListFragmentToQuestionOperationFragment action = AllQuestionListFragmentDirections.actionAllQuestionListFragmentToQuestionOperationFragment();
            action.setDocumentId(categoryDocumentId);
            action.setQuizTitle(quizTitle);
            action.setBatch(batch);
            action.setDept(dept);
            action.setCourseId(courseId);

            navController.navigate(action);
        }else if(v.getId()==R.id.results_btn){
            AllQuestionListFragmentDirections.ActionAllQuestionListFragmentToResultsFragment action = AllQuestionListFragmentDirections.actionAllQuestionListFragmentToResultsFragment();
            action.setCategoryDocumentId(categoryDocumentId);
            action.setQuizTitle(quizTitle);
            action.setBatch(batch);
            action.setDept(dept);
            action.setCourseId(courseId);
            navController.navigate(action);
        }
    }


    void setPreviewDialog(final String documentId) {
        previewDialog = new Dialog(getContext());
        previewDialog.setContentView(R.layout.preview_dialog);
        previewDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        previewDialog.setCanceledOnTouchOutside(false);


        Button optionOneButton = previewDialog.findViewById(R.id.quiz_option_one);
        Button optionTwoButton = previewDialog.findViewById(R.id.quiz_option_two);
        Button optionThreeButton = previewDialog.findViewById(R.id.quiz_option_three);
        Button optionFourButton = previewDialog.findViewById(R.id.quiz_option_four);
        Button cancelButton = previewDialog.findViewById(R.id.quiz_cancel_btn);
        Button publishButton = previewDialog.findViewById(R.id.quiz_publish_btn);

        TextView questionTextView = previewDialog.findViewById(R.id.quiz_question_text);
        TextView timerTextView = previewDialog.findViewById(R.id.quiz_question_time);

        optionOneButton.setText(answer);

        optionTwoButton.setText(otherOptionOne);

        questionTextView.setText(question);

        String timerText = "" + timer;
        timerTextView.setText(timerText);

        if (otherOptionTwo.isEmpty()) {
            optionThreeButton.setVisibility(View.GONE);
        } else {
            optionThreeButton.setText(otherOptionTwo);
        }

        if (otherOptionThree.isEmpty()) {
            optionFourButton.setVisibility(View.GONE);
        } else {
            optionFourButton.setText(otherOptionThree);
        }

        option_a = otherOptionOne;
        option_b = otherOptionTwo;
        option_c = otherOptionThree;

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                QuestionModel questionModel = new QuestionModel(question, answer,option_a,option_b,option_c,timer );

                FirebaseFirestore.getInstance().collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                        .document(categoryDocumentId)
                        .collection(CollectionNameModel.Question_COLLECTION_NAME)
                        .document(documentId)
                        .set(questionModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                                    updateDialog.dismiss();
                                } else {
                                    Toasty.error(getContext(), "Something Wrong", Toast.LENGTH_LONG).show();
                                }
                                previewDialog.dismiss();
                            }
                        });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewDialog.dismiss();
            }
        });

        previewDialog.show();
        Window window = previewDialog.getWindow();
        previewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}