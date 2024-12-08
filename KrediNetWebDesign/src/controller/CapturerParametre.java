package controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import controller.login.Connexion;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import entities.Adresse;
import entities.CompteMembre;
import entities.Membre;
import entities.Operation;
import entities.ProfileMembre;
import entities.service.ServiceLocal;

@ManagedBean(name="captureMB")
@SessionScoped
public class CapturerParametre implements Serializable{

	private static final long serialVersionUID = 1L;


private String email;
private String dateP;
private String idSession;
	public String getIdSession() {
		return ((String)get("paramIdSession"));
}

public void setIdSession(String idSession) {
	set("paramIdSession", idSession);
}

	public String getDateP() {
		return ((String)get("paramDateSession"));
}

public void setDateP(String dateP) {
	set("paramDateSession", dateP);
}

	public String getEmail() {
		return ((String)get("paramEmailSession"));
}


public void setEmail(String email) {
	set("paramEmailSession", email);
}


	public CapturerParametre() {
		super();
		
	}

	
	private static void set(String cle, Object valeur){
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		HttpSession session = request.getSession(false);
		session.setAttribute(cle, valeur);
        }

    private static Object get(String cle){
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
    	HttpSession session = request.getSession(false);
    	 Object res = null;
       
        res = session.getAttribute(cle);
        
        return res;
    }


}
