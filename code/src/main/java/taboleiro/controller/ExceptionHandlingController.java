package taboleiro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Controller
public class ExceptionHandlingController {

    @ExceptionHandler(Exception.class)
    public String Exception(Exception e , HttpServletRequest request, Model model){
        model.addAttribute("exception", e.toString());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("stack", e.getStackTrace());
        model.addAttribute("errorCode", request.getAttribute("javax.servlet.error.status_code"));
        return ("/error");
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Data integrity violation")  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
        // Nothing to do
    }


    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public String databaseError() {

        return "databaseError";
    }
}