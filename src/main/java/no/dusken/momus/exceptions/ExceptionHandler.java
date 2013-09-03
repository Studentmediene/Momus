package no.dusken.momus.exceptions;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * All exceptions while processing a request will be sent to this handler.
 * Will make a proper http response out of it, can log it here etc.
 */
public class ExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) {
        logException(httpServletRequest, response, o, e);

        if (e instanceof RestException) { // If it's our exception, we know how to handle it
            response.setStatus(((RestException) e).getStatus());
        } else { // Something else, let Spring handle it by throwing it again
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.print(e);
            throw new RuntimeException(e);
        }

        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        mav.addObject("error", "Something went wrong: \"" + e.getMessage() + "\"");

        return mav;
    }

    private void logException(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) {
        // TODO: Add logging
    }
}
