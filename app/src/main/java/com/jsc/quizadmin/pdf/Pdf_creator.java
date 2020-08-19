package com.jsc.quizadmin.pdf;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.model.StudentResultModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Pdf_creator {

    Context context;


    public Pdf_creator(Context context) {
        this.context = context;
    }


    public void permissionForResultDistribution(final String pdf_name, final String quizTitle, final String courseId, final String batchName, final String deptName, final ArrayList<String> studentId, final ArrayList<String> totalQuestion, final ArrayList<String> correctAnswer, final ArrayList<String> percentage) {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                createPdfForResultDistribution(pdf_name, quizTitle, courseId, batchName, deptName, studentId, totalQuestion, correctAnswer, percentage);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.error(context, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TedPermission.with(context)
                    .setPermissionListener(listener)
                    .setPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .check();
        }
    }

    public void createPdfForResultDistribution(final String pdf_name, final String quizTitle, final String courseId, String batchName, final String deptName, final ArrayList<String> teacherName, final ArrayList<String> batch, final ArrayList<String> courseCode, final ArrayList<String> percentage) {


        //Creating Directory
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + "Quiz Results"
                + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("_(yyyy-MM-dd)_(HH:mm)").format(date);

        //File Path
        String file_path = dir.getPath() + File.separator + pdf_name + "_" + deptName + "_" + "_" + batchName + "_" + timeStamp + ".pdf";


        //If Same file already Exist then Delete the Existing File
        if (new File(file_path).exists()) {
            new File(file_path).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();


            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(file_path));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Junaed Muhammad Chowdhury");
            document.addCreator("Junaed");

            /***
             * Variables for further use....
             */

            // Base Front For Front type
            final BaseFont baseFont = BaseFont.createFont(
                    BaseFont.TIMES_ROMAN,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);

            // Color for Front
            BaseColor headingColorAccent = new BaseColor(0, 153, 204, 255);

            // Heading Front Size
            float headingFontSize = 17.5f;

            // Other Front Size
            float valueFontSize = 16.0f;

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Varsity Logo Added ti the Documents
            Drawable d = context.getResources().getDrawable(R.drawable.logo);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAbsolutePosition(10f, 720f);
            image.scaleToFit(850, 100);
            document.add(image);

            //Adding Varsity Name
            Font varsityTitleFront = new Font(baseFont, 19.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk varsityTitleChunk = new Chunk("      Bangladesh Army University Of Engineering & Technology", varsityTitleFront);
            Paragraph varsityTitleParagraph = new Paragraph(varsityTitleChunk);
            varsityTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(varsityTitleParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));
            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));


            //Adding Document Title Name
            Font documentTitleFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentTitleChunk = new Chunk("All Students Quiz Results of " + deptName + " " + batchName + " Batch", documentTitleFront);
            Paragraph documentTitleParagraph = new Paragraph(documentTitleChunk);
            documentTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentTitleParagraph);


            //Adding Document Title Name
            Font documentSubTitleFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentSubTitleChunk = new Chunk("Quiz title : " + quizTitle + ", CourseCode : " + courseId, documentSubTitleFront);
            Paragraph documentSubTitleParagraph = new Paragraph(documentSubTitleChunk);
            documentSubTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentSubTitleParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));
            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));


            //Creating Table For 3 Rows
            PdfPTable table = new PdfPTable(new float[]{5, 5, 5, 5});
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidthPercentage(100);

            Font batchTitleFont = new Font(baseFont, headingFontSize, Font.NORMAL, headingColorAccent);
            Font batchFont = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.BLACK);
            //For Title Row
            //Title For Id
            PdfPCell cellValueForId = new PdfPCell(new Phrase("Student ID", batchTitleFont));
            cellValueForId.setFixedHeight(50);
            cellValueForId.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForId.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForId);

            //Title For Percentage
            PdfPCell cellValueForPercentage = new PdfPCell(new Phrase("Total Question", batchTitleFont));
            cellValueForPercentage.setFixedHeight(50);
            cellValueForPercentage.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForPercentage.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForPercentage);

            PdfPCell cellValueForCourseCode = new PdfPCell(new Phrase("Correct Answer", batchTitleFont));
            cellValueForCourseCode.setFixedHeight(50);
            cellValueForCourseCode.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForCourseCode.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForCourseCode);

            PdfPCell cellValueForPercentageValue = new PdfPCell(new Phrase("Percentage", batchTitleFont));
            cellValueForPercentageValue.setFixedHeight(50);
            cellValueForPercentageValue.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForPercentageValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForPercentageValue);

            table.setHeaderRows(1);

            //Coloring Title Row
            PdfPCell[] cells = table.getRow(0).getCells();
            for (int j = 0; j < cells.length; j++) {
                cells[j].setBackgroundColor(BaseColor.LIGHT_GRAY);
            }

            //Setting Values in Table fields
            for (int i = 0; i < teacherName.size(); i++) {
                String teacherName_Value = null;
                teacherName_Value = teacherName.get(i);
                String percentage_value = null;
                percentage_value = batch.get(i);
                String course_Code = null;
                course_Code = courseCode.get(i);
                String percentage_number = null;
                percentage_number = percentage.get(i);

                PdfPCell idCellValue = new PdfPCell(new Phrase(teacherName_Value, batchFont));
                idCellValue.setFixedHeight(40);
                idCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                idCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(idCellValue);

                PdfPCell percentageCellValue = new PdfPCell(new Phrase(percentage_value, batchFont));
                percentageCellValue.setFixedHeight(40);
                percentageCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                percentageCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(percentageCellValue);

                PdfPCell courseCodeCellValue = new PdfPCell(new Phrase(course_Code, batchFont));
                courseCodeCellValue.setFixedHeight(40);
                courseCodeCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                courseCodeCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(courseCodeCellValue);

                PdfPCell percentage_numberCellValue = new PdfPCell(new Phrase(percentage_number, batchFont));
                percentage_numberCellValue.setFixedHeight(40);
                percentage_numberCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                percentage_numberCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(percentage_numberCellValue);
            }

            document.add(table);

            document.close();


            Toasty.success(context, "Created... :)\nFile saved to the " + file_path, Toasty.LENGTH_LONG).show();


        } catch (IOException | DocumentException ignored) {
            Toasty.error(context, "GOT DocumentException", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException ae) {
            Toasty.info(context, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }


    public void permissionForSingleResult(final String pdf_name, final String quizTitle, final String courseId, final StudentResultModel model, final ArrayList<String> questionArrayList, final ArrayList<String> correctAnswerArrayList, final ArrayList<String> selectedAnswerArrayList) {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                createPdfForSingleResult(pdf_name, quizTitle, courseId, model, questionArrayList, correctAnswerArrayList, selectedAnswerArrayList);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.error(context, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TedPermission.with(context)
                    .setPermissionListener(listener)
                    .setPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .check();
        }
    }

    public void createPdfForSingleResult(final String pdf_name, final String quizTitle, final String courseId, final StudentResultModel model, final ArrayList<String> questionArrayList, final ArrayList<String> correctAnswerArrayList, final ArrayList<String> selectedAnswerArrayList) {


        //Creating Directory
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + "Quiz Results For Each Students"
                + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("_(yyyy-MM-dd)_(HH:mm)").format(date);

        //File Path
        String file_path = dir.getPath() + File.separator + pdf_name + "_" + model.getStudentDept() + "_" + "_" + model.getStudentBatch() + "_" + timeStamp + ".pdf";


        //If Same file already Exist then Delete the Existing File
        if (new File(file_path).exists()) {
            new File(file_path).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();


            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(file_path));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Junaed Muhammad Chowdhury");
            document.addCreator("Junaed");

            /***
             * Variables for further use....
             */

            // Base Front For Front type
            final BaseFont baseFont = BaseFont.createFont(
                    BaseFont.TIMES_ROMAN,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);

            // Color for Front
            BaseColor headingColorAccent = new BaseColor(0, 153, 204, 255);

            // Heading Front Size
            float headingFontSize = 17.5f;

            // Other Front Size
            float valueFontSize = 16.0f;

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Varsity Logo Added ti the Documents
            Drawable d = context.getResources().getDrawable(R.drawable.logo);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAbsolutePosition(10f, 720f);
            image.scaleToFit(850, 100);
            document.add(image);

            //Adding Varsity Name
            Font varsityTitleFront = new Font(baseFont, 19.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk varsityTitleChunk = new Chunk("      Bangladesh Army University Of Engineering & Technology", varsityTitleFront);
            Paragraph varsityTitleParagraph = new Paragraph(varsityTitleChunk);
            varsityTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(varsityTitleParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));
            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));


            //Adding Document Title Name
            Font documentTitleFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentTitleChunk = new Chunk("Result for " + model.getStudentName() + ", ID: " + model.getStudentId(), documentTitleFront);
            Paragraph documentTitleParagraph = new Paragraph(documentTitleChunk);
            documentTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentTitleParagraph);

            //Adding Document Title Name
            Font documentBatchTitleFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentBatchTitleChunk = new Chunk("Dept. : " + model.getStudentDept() + ", Batch : " + model.getStudentBatch(), documentBatchTitleFront);
            Paragraph documentBatchTitleParagraph = new Paragraph(documentBatchTitleChunk);
            documentBatchTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentBatchTitleParagraph);

            //Adding Document Title Name
            Font documentSubTitleFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentSubTitleChunk = new Chunk("Quiz title : " + quizTitle + ", CourseCode : " + courseId, documentSubTitleFront);
            Paragraph documentSubTitleParagraph = new Paragraph(documentSubTitleChunk);
            documentSubTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentSubTitleParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));
            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));


            //Creating Table For 3 Rows
            PdfPTable table = new PdfPTable(new float[]{5, 5, 5, 5});
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidthPercentage(100);

            Font batchTitleFont = new Font(baseFont, headingFontSize, Font.NORMAL, headingColorAccent);
            Font batchFont = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.BLACK);
            Font correctAnswerFont = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.GREEN);
            Font wrongAnswerFont = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.RED);
            //For Title Row
            //Title For Id
            PdfPCell cellValueForId = new PdfPCell(new Phrase("Question", batchTitleFont));
            cellValueForId.setFixedHeight(50);
            cellValueForId.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForId.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForId);

            //Title For Percentage
            PdfPCell cellValueForPercentage = new PdfPCell(new Phrase("Correct Answer", batchTitleFont));
            cellValueForPercentage.setFixedHeight(50);
            cellValueForPercentage.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForPercentage.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForPercentage);

            PdfPCell cellValueForCourseCode = new PdfPCell(new Phrase("Selected Answer", batchTitleFont));
            cellValueForCourseCode.setFixedHeight(50);
            cellValueForCourseCode.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForCourseCode.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForCourseCode);

            PdfPCell cellValueForPercentageValue = new PdfPCell(new Phrase("Result", batchTitleFont));
            cellValueForPercentageValue.setFixedHeight(50);
            cellValueForPercentageValue.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellValueForPercentageValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellValueForPercentageValue);

            table.setHeaderRows(1);

            //Coloring Title Row
            PdfPCell[] cells = table.getRow(0).getCells();
            for (int j = 0; j < cells.length; j++) {
                cells[j].setBackgroundColor(BaseColor.LIGHT_GRAY);
            }

            //Setting Values in Table fields
            for (int i = 0; i < questionArrayList.size(); i++) {
                String teacherName_Value = null;
                teacherName_Value = questionArrayList.get(i);
                String percentage_value = null;
                percentage_value = correctAnswerArrayList.get(i);
                String course_Code = null;
                course_Code = selectedAnswerArrayList.get(i);


                PdfPCell idCellValue = new PdfPCell(new Phrase(teacherName_Value, batchFont));
                idCellValue.setFixedHeight(40);
                idCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                idCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(idCellValue);

                PdfPCell percentageCellValue = new PdfPCell(new Phrase(percentage_value, batchFont));
                percentageCellValue.setFixedHeight(40);
                percentageCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                percentageCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(percentageCellValue);

                PdfPCell courseCodeCellValue = new PdfPCell(new Phrase(course_Code, batchFont));
                courseCodeCellValue.setFixedHeight(40);
                courseCodeCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                courseCodeCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(courseCodeCellValue);

                if (course_Code.equals(percentage_value)) {
                    PdfPCell percentage_numberCellValue = new PdfPCell(new Phrase("CORRECT", correctAnswerFont));
                    percentage_numberCellValue.setFixedHeight(40);
                    percentage_numberCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    percentage_numberCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(percentage_numberCellValue);
                } else {
                    PdfPCell percentage_numberCellValue = new PdfPCell(new Phrase("WRONG", wrongAnswerFont));
                    percentage_numberCellValue.setFixedHeight(40);
                    percentage_numberCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    percentage_numberCellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(percentage_numberCellValue);
                }
            }

            document.add(table);

            // Adding Line Breakable Space....
            document.add(new Paragraph(" "));

            Font documentTotalFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentTotalChunk = new Chunk("Total Question :   " + model.getTotalQuestion() + ",   Correct :   " + model.getCorrect(), documentTotalFront);
            Paragraph documentTotalParagraph = new Paragraph(documentTotalChunk);
            documentTotalParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentTotalParagraph);

            Font documentWrongFront = new Font(baseFont, 17.5f, Font.NORMAL, BaseColor.BLACK);
            Chunk documentWrongChunk = new Chunk("Wrong Answer :   " + model.getWrong() + ",   TimeOut :   " + model.getUnanswered(), documentWrongFront);
            Paragraph documentWrongParagraph = new Paragraph(documentWrongChunk);
            documentWrongParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(documentWrongParagraph);

            document.close();


            Toasty.success(context, "Created... :)\nFile saved to the " + file_path, Toasty.LENGTH_LONG).show();


        } catch (IOException | DocumentException ignored) {
            Toasty.error(context, "Got DocumentException", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException ae) {
            Toasty.info(context, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }
}
