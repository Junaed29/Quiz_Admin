package com.jsc.quizadmin.views;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.jsc.quizadmin.R;
import com.jsc.quizadmin.model.QuestionModel;
import com.shawnlin.numberpicker.NumberPicker;

import es.dmoral.toasty.Toasty;

public class QuestionOperationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "QuestionOperationFragme";

    private NavController navController;

    String quizTitle, visibility, documentId;

    NumberPicker numberPicker;

    String question = "", answer = "", otherOptionOne = "", otherOptionTwo = "", otherOptionThree = "", timerText = "";

    String option_a = "",option_b = "",option_c= "", option_d = "";
    int answeringTime = 0;

    TextView titleTextView;

    EditText questionEditText;
    EditText answerEditText;
    EditText otherOption1EditText;
    EditText otherOption2EditText;
    EditText otherOption3EditText;

    LinearLayout otherOption2LinearLayout;
    LinearLayout otherOption3LinearLayout;

    boolean isPublic = false;

    Button visibilityButton;
    Button saveButton;


    ImageButton addImageButton;
    ImageButton removeImageButton;
    ImageButton backImageButton;


    public QuestionOperationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_question_operation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        //questionDetailsModel = QuestionOperationFragmentArgs.fromBundle(getArguments()).getQuestionDetails();
        quizTitle = QuestionOperationFragmentArgs.fromBundle(getArguments()).getQuizTitle();
        visibility = QuestionOperationFragmentArgs.fromBundle(getArguments()).getVisibility();
        documentId = QuestionOperationFragmentArgs.fromBundle(getArguments()).getDocumentId();


        questionEditText = view.findViewById(R.id.questionEditText);
        answerEditText = view.findViewById(R.id.answerEditText);
        otherOption1EditText = view.findViewById(R.id.otherOption_1EditText);
        otherOption2EditText = view.findViewById(R.id.otherOption_2EditText);
        otherOption3EditText = view.findViewById(R.id.otherOption_3EditText);

        titleTextView = view.findViewById(R.id.titleTextViewId);

        numberPicker = view.findViewById(R.id.numberPickerId);

        numberPicker.setValue(10);

        otherOption2LinearLayout = view.findViewById(R.id.otherOption2LinearLayout);
        otherOption3LinearLayout = view.findViewById(R.id.otherOption3LinearLayout);


        addImageButton = view.findViewById(R.id.addButton);
        removeImageButton = view.findViewById(R.id.removeButton);
        backImageButton = view.findViewById(R.id.backButton);


        visibilityButton = view.findViewById(R.id.visibilityButtonId);
        saveButton = view.findViewById(R.id.button);


        titleTextView.setText(quizTitle);


        if (visibility.equals("public")) {
            isPublic = true;
            visibilityButton.setText("Public");
            visibility = "public";
            visibilityButton.setBackground(getResources().getDrawable(R.drawable.public_btn_bg, null));
        } else {
            isPublic = false;
            visibilityButton.setText("Private");
            visibility = "private";
            visibilityButton.setBackground(getResources().getDrawable(R.drawable.private_btn_bg, null));
        }


        saveButton.setOnClickListener(this);
        visibilityButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
        removeImageButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);


        Log.d(TAG, "onViewCreated: " + quizTitle + " " + visibility + " " + " " + documentId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton: {
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
                break;
            }

            case R.id.removeButton: {
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
                break;
            }

            case R.id.backButton: {
                navController.popBackStack();
                break;
            }

            case R.id.button: {
                /*Save Button*/
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

                if (question.isEmpty()) {
                    Toasty.error(getContext(), "Question is required", Toast.LENGTH_SHORT).show();
                } else if (answer.isEmpty()) {
                    Toasty.error(getContext(), "Answer is required", Toast.LENGTH_SHORT).show();
                } else if (otherOptionOne.isEmpty()) {
                    Toasty.error(getContext(), "At least One Other option needed", Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.warning(getContext(), "Option will be shuffle in real QUIZ", Toast.LENGTH_LONG).show();
                    setPreviewDialog();
                }

                break;
            }

            case R.id.visibilityButtonId: {
                isPublic = !isPublic;
                if (isPublic) {
                    visibilityButton.setText("Public");
                    visibility = "public";
                    visibilityButton.setBackground(getResources().getDrawable(R.drawable.public_btn_bg, null));
                    Toasty.info(getContext(), "QUIZ IS NOW PUBLIC", Toast.LENGTH_SHORT).show();
                } else {
                    visibilityButton.setText("Private");
                    visibility = "private";
                    visibilityButton.setBackground(getResources().getDrawable(R.drawable.private_btn_bg, null));
                    Toasty.info(getContext(), "QUIZ IS NOW PRIVATE", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    void setPreviewDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.preview_dialog);

        answeringTime = numberPicker.getValue();

        Button optionOneButton = dialog.findViewById(R.id.quiz_option_one);
        Button optionTwoButton = dialog.findViewById(R.id.quiz_option_two);
        Button optionThreeButton = dialog.findViewById(R.id.quiz_option_three);
        Button optionFourButton = dialog.findViewById(R.id.quiz_option_four);
        Button cancelButton = dialog.findViewById(R.id.quiz_cancel_btn);
        Button publishButton = dialog.findViewById(R.id.quiz_publish_btn);

        TextView questionTextView = dialog.findViewById(R.id.quiz_question_text);
        TextView timerTextView = dialog.findViewById(R.id.quiz_question_time);

        optionOneButton.setText(answer);

        optionTwoButton.setText(otherOptionOne);

        questionTextView.setText(question);

        timerText = "" + answeringTime;
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

        option_a = answer;
        option_b = otherOptionOne;
        option_c = otherOptionTwo;
        option_d = otherOptionThree;

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionModel questionModel = new QuestionModel(question, answer,option_a,option_b,option_c,option_d,answeringTime );

                Toasty.success(getContext(),questionModel.toString(),Toast.LENGTH_LONG).show();

                Log.d(TAG, "onClick: "+questionModel.toString());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}