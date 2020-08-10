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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.adapters.AllCategoryAdapter;
import com.jsc.quizadmin.model.CollectionNameModel;
import com.jsc.quizadmin.model.QuestionDetailsModel;

import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CategoryListFragment extends Fragment implements View.OnClickListener, AllCategoryAdapter.CategoryListener {

    private static final String TAG = "CategoryListFragment";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NavController navController;

    private FloatingActionButton floatingActionButton;

    private AllCategoryAdapter categoryAdapter;

    boolean isPublic = false;

    private Animation fade_in_Anim, fade_out_Anim;

    String courseId = "";
    String batch = "";
    String dept = "";
    String quizTitle = "";
    String visibility = "";
    String quizType = "";

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

        fade_in_Anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        fade_out_Anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);

        navController = Navigation.findNavController(view);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "onViewCreated: "+userId);

        recyclerView = (RecyclerView) view.findViewById(R.id.catogory_recycler_view);
        progressBar = view.findViewById(R.id.catogory_progressId);
        floatingActionButton = view.findViewById(R.id.catogory_fabId);

        floatingActionButton.setOnClickListener(this);


        setRecyclerView();
    }


    public void setRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                .whereEqualTo("userId", userId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else {
                    Toasty.error(getContext(), "Something Wrong\nPlease Check Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirestoreRecyclerOptions<QuestionDetailsModel> options = new FirestoreRecyclerOptions.Builder<QuestionDetailsModel>()
                .setQuery(query, QuestionDetailsModel.class)
                .build();

        categoryAdapter = new AllCategoryAdapter(options, getContext(), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);



        recyclerView.setAdapter(categoryAdapter);


        categoryAdapter.startListening();
    }

    @Override
    public void onClick(View v) {
        setDialog();
    }

    private void setDialog() {
        courseId = "";
        batch = "";
        dept = "";
        quizTitle = "";
        quizType = "test";

        final Dialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(R.layout.details_dialog);
        dialog.setCanceledOnTouchOutside(false);


        final TextInputEditText courseIdEditText = dialog.findViewById(R.id.edit_username);
        final TextInputEditText quizTitleEditText = dialog.findViewById(R.id.edit_title);
        final MaterialSpinner deptSpinner = (MaterialSpinner) dialog.findViewById(R.id.spinnerDepiId);
        final MaterialSpinner batchSpinner = (MaterialSpinner) dialog.findViewById(R.id.spinnerBatchId);
        final Button goButton = (Button) dialog.findViewById(R.id.goButtonId);
        final Button typeButton = (Button) dialog.findViewById(R.id.catagory_type);

        deptSpinner.setItems(getResources().getStringArray(R.array.allDepartment));
        batchSpinner.setItems(getResources().getStringArray(R.array.allBatches));

        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeButton.getText().toString().toLowerCase().contains("test")){
                    quizType = "exercise";
                    typeButton.setText("Quiz type : Exercise");
                    Toasty.info(getContext(), "Student can see their results", Toast.LENGTH_SHORT).show();
                }else if (typeButton.getText().toString().toLowerCase().contains("exercise")){
                    quizType = "test";
                    typeButton.setText("Quiz type : Test");
                    Toasty.info(getContext(), "Student can not see their results", Toast.LENGTH_SHORT).show();
                }
            }
        });


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

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseId = courseIdEditText.getText().toString().toUpperCase().trim();
                quizTitle = quizTitleEditText.getText().toString().trim();
                if (courseId == null|batch==null|dept==null|quizTitle==null|batch.isEmpty()|courseId.isEmpty()|dept.isEmpty()|quizTitle.isEmpty()){
                    Toast.makeText(getContext(), "Please Fill All Value", Toast.LENGTH_SHORT).show();
                }else {
                    final QuestionDetailsModel detailsModel = new QuestionDetailsModel(quizTitle,courseId,batch,dept,"private",userId,quizType );

                    FirebaseFirestore.getInstance()
                            .collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                            .add(detailsModel)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();

                                        CategoryListFragmentDirections.ActionCategoryListFragmentToQuestionOperationFragment action = CategoryListFragmentDirections.actionCategoryListFragmentToQuestionOperationFragment();
                                        action.setDocumentId(task.getResult().getId());
                                        action.setQuizTitle(detailsModel.getQuestionTitle());
                                        action.setBatch(detailsModel.getBatch());
                                        action.setDept(detailsModel.getDept());
                                        action.setCourseId(detailsModel.getCourseId());

                                        navController.navigate(action);
                                    }else {
                                        Toasty.error(getContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.dismiss();
                                }
                            });
                }
            }
        });


//        goButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toasty.success(getContext(), "Success", Toast.LENGTH_LONG).show();
//                CategoryListFragmentDirections.ActionCategoryListFragmentToQuestionOperationFragment action = CategoryListFragmentDirections.actionCategoryListFragmentToQuestionOperationFragment();
//                action.setDocumentId("i");
//                action.setQuizTitle("Junaed");
//                action.setQuizTitle("Junaed");
//                action.setQuizTitle("Junaed");
//                navController.navigate(action);
//                dialog.dismiss();
//            }
//        });

        dialog.show();
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    @Override
    public void categoryItemClicked(DocumentSnapshot snapshot) {
        QuestionDetailsModel model = snapshot.toObject(QuestionDetailsModel.class);
        CategoryListFragmentDirections.ActionCategoryListFragmentToAllQuestionListFragment action = CategoryListFragmentDirections.actionCategoryListFragmentToAllQuestionListFragment();
        action.setCategoryDocumentId(model.getDocumentId());
        action.setQuizTitle(model.getQuestionTitle());
        action.setCourseId(model.getCourseId());
        action.setBatch(model.getBatch());
        action.setDept(model.getDept());
        navController.navigate(action);
    }

    @Override
    public void categoryDeleteClicked(DocumentSnapshot snapshot) {
        QuestionDetailsModel model = snapshot.toObject(QuestionDetailsModel.class);
        setCategoryDeleteDialog(model);
    }

    @Override
    public void categoryEditClicked(DocumentSnapshot snapshot) {
        QuestionDetailsModel model = snapshot.toObject(QuestionDetailsModel.class);
        setCategoryEditDialog(model);
    }

    @Override
    public void categoryVisibilityClicked(DocumentSnapshot snapshot) {
        QuestionDetailsModel model = snapshot.toObject(QuestionDetailsModel.class);
        setCategoryEditDialog(model);
    }


    private void setCategoryEditDialog(final QuestionDetailsModel detailsModel) {

        final String oldquizTitle = detailsModel.getQuestionTitle();
        final String oldcourseId = detailsModel.getCourseId();
        final String oldbatch = detailsModel.getBatch();
        final String olddept = detailsModel.getDept();
        final String oldVisibility = detailsModel.getVisibility();
        final String oldquizType = detailsModel.getQuizType()==null ?  "null" : detailsModel.getQuizType();

        courseId = "";
        batch = detailsModel.getBatch();
        dept = detailsModel.getDept();
        quizTitle = "";
        quizType = "";

        final Dialog dialog = new Dialog(getActivity());


        dialog.setContentView(R.layout.category_edit_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.setCanceledOnTouchOutside(false);


        final TextInputEditText courseIdEditText = dialog.findViewById(R.id.catagory_edit_courseId);
        final TextInputEditText quizTitleEditText = dialog.findViewById(R.id.catagory_edit_title);
        final MaterialSpinner deptSpinner = (MaterialSpinner) dialog.findViewById(R.id.catagory_edit_spinnerDepiId);
        final MaterialSpinner batchSpinner = (MaterialSpinner) dialog.findViewById(R.id.catagory_edit_spinnerBatchId);
        final Button updateButton = (Button) dialog.findViewById(R.id.catagory_edit_update);
        final Button cancelButton = (Button) dialog.findViewById(R.id.catagory_edit_cancle);
        final Button visibilityButton = (Button) dialog.findViewById(R.id.catagory_edit_visibility);
        final Button typeButton = (Button) dialog.findViewById(R.id.catagory_type);

        if (detailsModel.getVisibility().equals("public")) {
            isPublic = true;
            visibilityButton.setText("Public");
            visibility = "public";
            visibilityButton.setBackground(getResources().getDrawable(R.drawable.public_btn_bg, null));
            visibilityButton.setTextColor(getResources().getColor(R.color.public_btn_clr, null));
        } else {
            isPublic = false;
            visibilityButton.setText("Private");
            visibility = "private";
            visibilityButton.setBackground(getResources().getDrawable(R.drawable.private_btn_bg, null));
            visibilityButton.setTextColor(getResources().getColor(R.color.private_btn_clr, null));
        }


        if (detailsModel.getQuizType() == null){
            quizType = "exercise";
            typeButton.setText("Quiz type : Exercise");
        }else if (detailsModel.getQuizType().toLowerCase().contains("test")){
            quizType = "test";
            typeButton.setText("Quiz type : Test");
        }else if (detailsModel.getQuizType().toLowerCase().contains("exercise")){
            quizType = "exercise";
            typeButton.setText("Quiz type : Exercise");
        }else {
            quizType = "exercise";
        }

        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeButton.getText().toString().toLowerCase().contains("test")){
                    quizType = "exercise";
                    typeButton.setText("Quiz type : Exercise");
                    Toasty.info(getContext(), "Student can see their results", Toast.LENGTH_SHORT).show();
                }else if (typeButton.getText().toString().toLowerCase().contains("exercise")){
                    quizType = "test";
                    typeButton.setText("Quiz type : Test");
                    Toasty.info(getContext(), "Student can not see their results", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<String> deptList = Arrays.asList(getResources().getStringArray(R.array.allDepartment));
        List<String> batchList = Arrays.asList(getResources().getStringArray(R.array.allBatches));

        deptSpinner.setItems(deptList);
        batchSpinner.setItems(batchList);


        int deptSelectedIndex = deptList.indexOf(detailsModel.getDept());
        int batchSelectedIndex = batchList.indexOf(detailsModel.getBatch());

        deptSpinner.setSelectedIndex(deptSelectedIndex);
        batchSpinner.setSelectedIndex(batchSelectedIndex);

        courseIdEditText.setText(detailsModel.getCourseId());
        quizTitleEditText.setText(detailsModel.getQuestionTitle());


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

        visibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPublic = !isPublic;
                if (isPublic) {
                    visibilityButton.setText("Public");
                    visibility = "public";
                    visibilityButton.setBackground(getResources().getDrawable(R.drawable.public_btn_bg, null));
                    visibilityButton.setTextColor(getResources().getColor(R.color.public_btn_clr, null));
                    Toasty.info(getContext(), "QUIZ IS NOW PUBLIC", Toast.LENGTH_SHORT).show();
                } else {
                    visibilityButton.setText("Private");
                    visibility = "private";
                    visibilityButton.setBackground(getResources().getDrawable(R.drawable.private_btn_bg, null));
                    visibilityButton.setTextColor(getResources().getColor(R.color.private_btn_clr, null));
                    Toasty.info(getContext(), "QUIZ IS NOW PRIVATE", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseId = courseIdEditText.getText().toString().toUpperCase().trim();
                quizTitle = quizTitleEditText.getText().toString().trim();

                if (courseId == null | batch == null | dept == null | quizTitle == null | batch.isEmpty() | courseId.isEmpty() | dept.isEmpty() | quizTitle.isEmpty()) {
                    Toasty.error(getContext(), "Please Fill All Value", Toast.LENGTH_SHORT).show();
                } else {
                    String documentId = detailsModel.getDocumentId();
                    QuestionDetailsModel newDetailsModel = new QuestionDetailsModel(quizTitle, courseId, batch, dept, visibility,userId,quizType);
                    if (quizTitle.equals(oldquizTitle) && batch.equals(oldbatch) && dept.equals(olddept) && courseId.equals(oldcourseId) && visibility.equals(oldVisibility)&& quizType.equals(oldquizType)) {
                        Toasty.info(getContext(), "No changed found ", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseFirestore.getInstance().collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                                .document(documentId)
                                .set(newDetailsModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toasty.success(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            Toasty.error(getContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
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
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private void setCategoryDeleteDialog(final QuestionDetailsModel detailsModel) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.category_delete_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        dialog.setCanceledOnTouchOutside(false);

        TextView titleTextView = dialog.findViewById(R.id.delete_dialog_titleId);
        TextView messageTextView = dialog.findViewById(R.id.delete_dialog_messageId);
        String message = "Are you sure to delete this quiz category permanently ?\nAll The Questions of this category will be deleted also.";
        String title = "Delete This Quiz ??";
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
                String documentId = detailsModel.getDocumentId();
                FirebaseFirestore.getInstance().collection(CollectionNameModel.MAIN_COLLECTION_NAME)
                        .document(documentId)
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
}