package nlp;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.Map;

/**
 * The controller class
 * @author synerzip
 */
public class Main extends Controller{
    /**
     * Method that gets an input from a form and processes that input and returns the output in JSON format
     * @return Result to index
     */
    public Result getForm() {
        String input = getInput();
        NLP nlp = new NLP();
        String output = nlp.parseInput(input);
        return ok(index.render(output));
    }
    /**
     * Method that returns input data from form
     * @return input String from form
     */
    private static String getInput(){
        final Map<String, String[]> form_values = request().body().asFormUrlEncoded();
        String input = form_values.get("input_text")[0];
        return input;
    }
}
