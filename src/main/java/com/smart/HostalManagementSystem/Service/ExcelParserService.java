package com.smart.HostalManagementSystem.Service;

import com.smart.HostalManagementSystem.Entity.Student;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelParserService {

    // Excel file eken Student list ekk parse karanawa
    public List<Student> parseStudentExcel(MultipartFile file) throws Exception {

        List<Student> students = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Row 0 = header row, ithin 1 idan start karanawa
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Empty row eka skip karanawa
                if (getCellValue(row.getCell(0)).isEmpty()) continue;

                Student student = new Student();
                student.setRegistrationNumber(getCellValue(row.getCell(0)));
                student.setFullName(getCellValue(row.getCell(1)));
                student.setEmail(getCellValue(row.getCell(2)));
                student.setPhoneNumber(getCellValue(row.getCell(3)));
                student.setNic(getCellValue(row.getCell(4)));
                student.setGender(getCellValue(row.getCell(5)));
                student.setFaculty(getCellValue(row.getCell(6)));
                student.setAcademicYear(getCellValue(row.getCell(7)));
                student.setAddress(getCellValue(row.getCell(8)));

                students.add(student);
            }
        }

        return students;
    }

    // Cell eke value eka String widiyata ganna (number/text dekama handle karanawa)
    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Registration number wagē ewa number widiyata save wela thiyenna puluwan
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}