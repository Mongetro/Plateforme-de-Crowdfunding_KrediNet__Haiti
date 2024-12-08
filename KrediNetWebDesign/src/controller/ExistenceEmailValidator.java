package controller;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import entities.service.ServiceLocal;


@FacesValidator( value = "ExistenceEmailValidator" )

public class ExistenceEmailValidator implements Validator {
	

    private static final String EMAIL_EXISTE_DEJA = "Cette adresse email est d�j� utilis�e";

    @EJB
    private ServiceLocal      utilisateurDao;

    @Override
    public void validate( FacesContext context, UIComponent component, Object value ) throws ValidatorException {
        /* R�cup�ration de la valeur � traiter depuis le param�tre value */
        String email = (String) value;
        if (Utility.validate(email)){
	        try {
	            if ( utilisateurDao.rechercher(email) != null ) {
	                /*
	                 * Si une adresse est retourn�e, alors on envoie une exception
	                 * propre � JSF, qu'on initialise avec un FacesMes	
	                 * 																						sage de
	                 * gravit� "Erreur" et contenant le message d'explication. Le
	                 * framework va alors g�rer lui-m�me cette exception et s'en
	                 * servir pour afficher le message d'erreur � l'utilisateur.
	                 */
	                throw new ValidatorException(
	                        new FacesMessage( FacesMessage.SEVERITY_ERROR, EMAIL_EXISTE_DEJA, null ) );
	            }
	        } catch (Exception e ) {
	            /*
	             * En cas d'erreur impr�vue �manant de la BDD, on pr�pare un message
	             * d'erreur contenant l'exception retourn�e, pour l'afficher �
	             * l'utilisateur ensuite.
	             */
	            FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null );
	            FacesContext facesContext = FacesContext.getCurrentInstance();
	            facesContext.addMessage( component.getClientId( facesContext ), message );
	        }
        }
    }


}
