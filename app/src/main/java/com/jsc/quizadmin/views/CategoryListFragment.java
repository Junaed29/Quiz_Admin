package com.jsc.quizadmin.views;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jsc.quizadmin.R;

import es.dmoral.toasty.Toasty;

public class CategoryListFragment extends Fragment implements View.OnClickListener {

    private NavController navController;

    private FloatingActionButton floatingActionButton;

    String courseId = "";
    String batch = "";
    String dept = "";
    String quizTitle = "";

    String userId = "";

    public CategoryListFragment() {
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
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        floatingActionButton = view.findViewById(R.id.fabId);

        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setDialog();
    }

    private void setDialog() {
        final Dialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(R.layout.details_dialog);

        dialog.setCanceledOnTouchOutside(false);


        final TextInputEditText courseIdEditText = dialog.findViewById(R.id.edit_username);
        final TextInputEditText quizTitleEditText = dialog.findViewById(R.id.edit_title);
        final MaterialSpinner deptSpinner = (MaterialSpinner) dialog.findViewById(R.id.spinnerDepiId);
        final MaterialSpinner batchSpinner = (MaterialSpinner) dialog.findViewById(R.id.spinnerBatchId);
        final Button goButton = (Button) dialog.findViewById(R.id.goButtonId);

        deptSpinner.setItems(getResources().getStringArray(R.array.allDepartment));
        batchSpinner.setItems(getResources().getStringArray(R.array.allBatches));


        deptSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                dept = item.toString();
            }
        });

        batchSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                batch = item.toString();
            }
        });

//        goButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                courseId = courseIdEditText.getText().toString().toUpperCase();
//                quizTitle = quizTitleEditText.getText().toString();
//                if (courseId == null|batch==null|dept==null|quizTitle==null|batch.isEmpty()|courseId.isEmpty()|dept.isEmpty()|quizTitle.isEmpty()){
//                    Toast.makeText(getContext(), "Please Fill All Value", Toast.LENGTH_SHORT).show();
//                }else {
//                    final QuestionDetailsModel detailsModel = new QuestionDetailsModel(quizTitle,courseId,batch,dept,"private" );
//
//                    FirebaseFirestore.getInstance()
//                            .collection(CollectionNameModel.MAIN_COLLECTION_NAME)
//                            .document(userId)
//                            .collection(CollectionNameModel.TEACHER_COLLECTION_NAME)
//                            .add(detailsModel)
//                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentReference> task) {
//                                    if (task.isSuccessful()){
//                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
//
//                                        CategoryListFragmentDirections.ActionCategoryListFragmentToQuestionOperationFragment action = CategoryListFragmentDirections.actionCategoryListFragmentToQuestionOperationFragment();
//                                        action.setDocumentId(task.getResult().getId());
//                                        action.setQuizTitle(detailsModel.getQuestionTitle());
//                                        action.setVisibility(detailsModel.getVisibility());
//                                        navController.navigate(action);
//                                    }else {
//                                        Toast.makeText(getContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    dialog.dismiss();
//                                }
//                            });
//                }
//            }
//        });


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.success(getContext(), "Success", Toast.LENGTH_LONG).show();
                CategoryListFragmentDirections.ActionCategoryListFragmentToQuestionOperationFragment action = CategoryListFragmentDirections.actionCategoryListFragmentToQuestionOperationFragment();
                action.setDocumentId("i");
                action.setQuizTitle("Junaed");
                action.setVisibility("public");
                navController.navigate(action);
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}