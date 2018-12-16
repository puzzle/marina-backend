package ch.puzzle.marinabackend.employee;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class MonthlyPayoutService {

    private static final String TEMPLATE_NAME = "payout_summary";
    private static final String VAR_EMPLOYEE = "employee";
    private static final String VAR_PAYOUTS = "payouts";
    private static final String VAR_TOTAL_CHF = "totalChf";
    private static final String VAR_TOTAL_BTC = "totalBtc";

    @Autowired
    private MonthlyPayoutRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ApplicationContext applicationContext;

    public byte[] generatePayoutSummaryForAllEmployees(Locale locale, Integer year) throws IOException, DocumentException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<InputStream> reports = new ArrayList<>();
        repository.findByYearOrderByMonthDesc(year)
                .stream()
                .map(MonthlyPayout::getEmployee)
                .map(Employee::getId)
                .distinct()
                .forEach(employeeId -> {
                    try {
                        reports.add(new ByteArrayInputStream(generatePayoutSummary(locale, employeeId, year)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            for (InputStream in : reports) {
                PdfReader reader = new PdfReader(in);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    document.newPage();
                    //import the page from source pdf
                    PdfImportedPage page = writer.getImportedPage(reader, i);
                    //add the page to the destination pdf
                    cb.addTemplate(page, 0, 0);
                }
            }
            os.flush();
            document.close();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                // ignore
            }
        }

        return os.toByteArray();
    }

    public byte[] generatePayoutSummary(Locale locale, Long employeeId, Integer year) throws IOException, DocumentException {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        Employee employee = employeeOptional.orElseThrow(() -> new RuntimeException("Employee not found!"));
        List<MonthlyPayout> payouts = repository.findByEmployeeIdAndYearOrderByMonthDesc(employeeId, year);
        String htmlTemplate = fillHtmlTemplate(locale, employee, payouts);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setProperty("java.protocol.handler.pkgs", "org.xhtmlrenderer.protocols");

        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlTemplate);
            renderer.layout();
            renderer.createPDF(os);

            os.close();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                // ignore
            }
        }

        return os.toByteArray();
    }

    private String fillHtmlTemplate(Locale locale, Employee employee, List<MonthlyPayout> payouts) {
        Context context = new Context(locale);
        context.setVariable(
                ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(applicationContext, null)
        );
        context.setVariable(VAR_EMPLOYEE, employee);
        context.setVariable(VAR_PAYOUTS, payouts);
        context.setVariable(VAR_TOTAL_CHF, payouts.stream().map(MonthlyPayout::getAmountChf).mapToDouble(BigDecimal::doubleValue).sum());
        context.setVariable(VAR_TOTAL_BTC, payouts.stream().mapToLong(MonthlyPayout::getAmountBtc).sum());
        return templateEngine.process(TEMPLATE_NAME, context);
    }
}
