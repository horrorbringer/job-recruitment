package com.recruitment.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.recruitment.model.JobSeeker;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfResumeService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
    private static final Font CONTACT_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

    public byte[] generateResumeBytes(JobSeeker profile) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);

            document.open();

            // 1. Header (Name & Contact)
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell nameCell = new PdfPCell(new Phrase(
                    profile.getFullName() != null ? profile.getFullName().toUpperCase() : "YOUR NAME", TITLE_FONT));
            nameCell.setBorder(Rectangle.NO_BORDER);
            nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(nameCell);

            String contactInfo = "";
            if (StringUtils.hasText(profile.getUser().getEmail())) {
                contactInfo += profile.getUser().getEmail();
            }
            if (StringUtils.hasText(profile.getPhone())) {
                contactInfo += "  |  " + profile.getPhone();
            }
            if (StringUtils.hasText(profile.getLocation())) {
                contactInfo += "  |  " + profile.getLocation();
            }

            PdfPCell contactCell = new PdfPCell(new Phrase(contactInfo, CONTACT_FONT));
            contactCell.setBorder(Rectangle.NO_BORDER);
            contactCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            contactCell.setPaddingTop(5);
            headerTable.addCell(contactCell);

            document.add(headerTable);
            document.add(new Chunk(new LineSeparator(1f, 100f, Color.LIGHT_GRAY, Element.ALIGN_CENTER, -10f)));
            document.add(Chunk.NEWLINE);

            // 2. Professional Summary
            if (StringUtils.hasText(profile.getAbout())) {
                document.add(new Paragraph("PROFESSIONAL SUMMARY", SUBTITLE_FONT));
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph(parseHtmlToText(profile.getAbout()), NORMAL_FONT));
                document.add(new Chunk(new LineSeparator(0.5f, 100f, Color.LIGHT_GRAY, Element.ALIGN_CENTER, -10f)));
                document.add(Chunk.NEWLINE);
            }

            // 3. Skills
            if (StringUtils.hasText(profile.getSkills())) {
                document.add(new Paragraph("SKILLS", SUBTITLE_FONT));
                document.add(Chunk.NEWLINE);

                // Usually skills are comma separated
                String cleanedSkills = parseHtmlToText(profile.getSkills());
                document.add(new Paragraph(cleanedSkills, NORMAL_FONT));
                document.add(new Chunk(new LineSeparator(0.5f, 100f, Color.LIGHT_GRAY, Element.ALIGN_CENTER, -10f)));
                document.add(Chunk.NEWLINE);
            }

            // 4. Experience (Parsed HTML)
            if (profile.getExperienceLevel() != null && profile.getExperienceYears() != null) {
                document.add(new Paragraph("EXPERIENCE LEVEL: " + profile.getExperienceLevel().name() + " ("
                        + profile.getExperienceYears() + " Years)", SUBTITLE_FONT));
                document.add(Chunk.NEWLINE);
            }

            // In a real scenario we'd have a list of WorkExperience entities.
            // Currently it seems the user might be putting their experience raw into rich
            // text fields.
            // If there's an experience field that holds raw HTML, parse it here:
            // Example: parsing the `about` or if education/experience was HTML

            // 5. Education
            if (StringUtils.hasText(profile.getEducation())) {
                document.add(new Paragraph("EDUCATION", SUBTITLE_FONT));
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph(parseHtmlToText(profile.getEducation()), NORMAL_FONT));
                document.add(new Chunk(new LineSeparator(0.5f, 100f, Color.LIGHT_GRAY, Element.ALIGN_CENTER, -10f)));
                document.add(Chunk.NEWLINE);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF resume: " + e.getMessage(), e);
        }
    }

    /**
     * JSoup extracts pure text from HTML provided by the Quill editor,
     * stripping out messy `
     * <p>
     * ` and `<strong>` tags while preserving newlines basically.
     */
    private String parseHtmlToText(String htmlContent) {
        if (!StringUtils.hasText(htmlContent)) {
            return "";
        }
        // Jsoup's Default configuration removes blocks. We can use wholeText or a
        // NodeVisitor
        // to convert <br> and <p> to actual newline characters for the PDF writer.
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
        // Add fake line breaks to <p> tags for spacing
        doc.select("p").prepend("\\n\\n");
        doc.select("br").append("\\n");
        doc.select("li").prepend("\\n• ");

        String cleanText = doc.text().replace("\\n", "\n");
        return cleanText.trim();
    }
}
