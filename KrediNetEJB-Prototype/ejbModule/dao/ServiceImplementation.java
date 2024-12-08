package dao;
import entities.service.*;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import entities.Adresse;
import entities.CompteMembre;
import entities.Depot;
import entities.DepotMoncash;
import entities.DesactiverLien;
import entities.Membre;
import entities.MobiliteMembre;
import entities.Operation;
import entities.PeerToPeer;
import entities.Placement;
import entities.Pret;
import entities.ProfileMembre;
import entities.Remboursement;
import entities.Retrait;
import entities.Role;
import entities.SendEmail;
import entities.Transfert;

@Stateless
public  class ServiceImplementation implements ServiceLocal,ServiceRemote{
@PersistenceContext(unitName="UP_KREDINET")
private EntityManager em;

@Override
public void enregistrer(Membre mb)  {

	mb.setMotdePasse(HashPassword.hashPassword(mb.getMotdePasse()));
  em.persist(mb);
    Role ro=new Role("MEMBRE",new Membre(mb.getEmail_me()));
em.persist(ro);
    
    SendEmail.emailConf(mb.getEmail_me());

}

@Override
public Membre rechercher(String email) {
	Membre mb=em.find(Membre.class, email);
	return mb;
}



@Override
public List<Membre> listerMembre() {
	
	Query req=em.createQuery("SELECT mb FROM Membre mb");
	 List<Membre> listerMembre=req.getResultList();
	 return listerMembre;
}




@Override
public String enregistrer(CompteMembre cp,ProfileMembre pm,DesactiverLien d) {
	em.persist(cp);
	pm.setNoCompte_me(cp);
	em.persist(pm);
	em.persist(d);
  SendEmail.sendEmail(cp.getNoCompte_me(), cp.getEmail_me().getEmail_me() );
	return cp.getNoCompte_me();
}

@Override
public CompteMembre getCompte(String noCompte) {
CompteMembre cp=em.find(CompteMembre.class, noCompte);
//if(cp==null)throw new RuntimeException("Compte introuvable");
	return cp;
}

@Override
public void modifier(CompteMembre cp,ProfileMembre pm) {
	//cp=em.find(CompteMembre.class, cp.getNoCompte_me());
	//if(cm!=null)
	em.merge(cp);
	pm.setNoCompte_me(cp);
	em.merge(pm);
	
		
}

@Override
public List<CompteMembre> listerCompte() {
	Query req=em.createQuery("SELECT cm FROM CompteMembre cm");
	List<CompteMembre> listerCompte=req.getResultList();
	return listerCompte;
}


@Override
public List<CompteMembre> listerCompte(String etat) {
	Query req=em.createQuery("SELECT cm FROM CompteMembre cm WHERE cm.etat=:x");
	req.setParameter("x", etat);
	List<CompteMembre> listerCompte=req.getResultList();
	return listerCompte;
}


@Override
public void enregistrer(ProfileMembre auth) {
	em.persist(auth);
	
}

@Override
public ProfileMembre rechercherAuth(String cin_nif) {
	ProfileMembre auth=em.find(ProfileMembre.class,cin_nif);
	return auth;
}

@Override
public List<ProfileMembre> lister() {
	Query req=em.createQuery("SELECT auth FROM ProfileMembre auth");
	List<ProfileMembre> list=req.getResultList();
	return list;
}

@Override
public void modifier(ProfileMembre auth,MobiliteMembre m) {
    em.merge(auth);
	em.persist(m);
	
}

@Override
public double calculer() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public double caluler() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public void placement(Placement pl,String noCompte) {
	CompteMembre cm=getCompte(noCompte);
	em.persist(pl);
cm.setSolde_compte(cm.getSolde_compte()-pl.getMontant_op());	
em.merge(cm);
}




@Override
public double calculer(CompteMembre solde) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public void modifier(Membre mb) {
	// TODO Auto-generated method stub
	
}

@Override
public List<Placement> listerPlacement(String numeroCompte,String typeOperation) {
	Query req=em.createQuery("SELECT op FROM Operation op WHERE NoCompte=:x AND TypeOperation=:y");
	req.setParameter("x", numeroCompte);
	req.setParameter("y", typeOperation);
	List<Placement> list=req.getResultList();
			return list;
}

@Override
public void depot(Depot dp,DepotMoncash dm,String noCompte ) {
	CompteMembre cm=em.find(CompteMembre.class,noCompte);	
	if(cm==null){
    em.persist(dm);	
	}else{		
		em.persist(dm);	

		em.persist(dp);

		cm.setSolde_compte(cm.getSolde_compte()+dp.getMontant_op());
		em.merge(cm);

		DepotMoncash d=em.find(DepotMoncash.class, dm.getIdTransaction());
		d.setEtat("Passif");
		em.merge(d);	
	}

}

@Override
public List<Depot> listerDepot(String numeroCompte, String typeOperation) {
	Query req=em.createQuery("SELECT op FROM Operation op WHERE NoCompte=:x AND TypeOperation=:y");
	req.setParameter("x", numeroCompte);
	req.setParameter("y", typeOperation);
	List<Depot> list=req.getResultList();
			return list;
}

@Override
public void pret(Pret pt) {
	Long idOperation = null;
	String investisseur = null;
	Placement p = null;
	PeerToPeer pe;
	CompteMembre co=null;
	double interet=pt.getMontant_op()*0.07;
	double sommeDue=pt.getMontant_op()+interet;
	
//S'il existe un seul investissement pouvant satisfaire  ce pret
List<Placement> lstPl1=listerPlacement(false, "Placement",pt.getMontant_op());
System.out.println("index"+lstPl1.get(0).toString());
if(lstPl1.size()==1){
	for(Placement pl1:lstPl1){
		idOperation=pl1.getId_op();
		investisseur=pl1.getNoCompte_me().getNoCompte_me();
	}
	
//on enregistre peerToPeer	
	pe=new PeerToPeer(0.0,sommeDue,new CompteMembre(pt.getNoCompte_me().getNoCompte_me()), new CompteMembre(investisseur), pt.getMontant_op());
	em.persist(pe);
//on met la cle etrangere de peerToPeer dans operation et on persiste le pret	
	pt.setPeerToPeer(pe);
	em.persist(pt);
	
//on ajoute le montant au solde du pretteur 	
	co=em.find(CompteMembre.class, pt.getNoCompte_me().getNoCompte_me());
	co.setSolde_compte(co.getSolde_compte()+pt.getMontant_op());
	em.merge(co);
	
//On met l'etat du placement de facon a ce qu'il ne soit pas disponible 	
	p=em.find(Placement.class, idOperation);
	p.setEtat_Pla(true);
	em.merge(p);
	
}


//S'il existe plusieurs investissement pouvant satisfaire ce pret
if(lstPl1.size()>1){
List<Placement> lstPl2=listerPlacement1(false, "Placement",pt.getMontant_op());	
	System.out.println("Size2="+lstPl2.size());
		for(Placement pl2:lstPl2){
			idOperation=pl2.getId_op();
			investisseur=pl2.getNoCompte_me().getNoCompte_me();
	       }
		//on enregistre peerToPeer	
		pe=new PeerToPeer(0.0,sommeDue,new CompteMembre(pt.getNoCompte_me().getNoCompte_me()), new CompteMembre(investisseur), pt.getMontant_op());
		em.persist(pe);
	//on met la cle etrangere de peerToPeer dans operation et on persiste le pret	
		pt.setPeerToPeer(pe);
		em.persist(pt);
		
	//on ajoute le montant au solde du pretteur 	
		co=em.find(CompteMembre.class, pt.getNoCompte_me().getNoCompte_me());
		co.setSolde_compte(co.getSolde_compte()+pt.getMontant_op());
		em.merge(co);
		
	//On met l'etat du placement de facon a ce qu'il ne soit pas disponible 	
		p=em.find(Placement.class, idOperation);
		p.setEtat_Pla(true);
		em.merge(p);
		
}
/*Cas plus complexe ou il n'y a pas d'investissement repondant a ce pret 
 ou on est oblige d'aller chercher la somme demande dans plusieurs investissement*/ 
//List<Placement> lstPl3=listerPlacement(false, "Placement");


/*do{
	for(Placement pl3:lstPl3){
		idOperation=pl3.getId_op();
		somme+=pl3.getMontant_op();
	   }	
	
}while(somme==pt.getMontant_op());
*/


}

@Override
public List<Pret> listerPret(String numeroCompte, String critere) {
	Query req=em.createQuery("SELECT p FROM Pret p WHERE NoCompte=:x AND TypeOperation=:y");
	req.setParameter("x", numeroCompte);
	req.setParameter("y", critere);
	List<Pret> list=req.getResultList();
	return list;
}

@Override
public void remboursement(Remboursement rb) {
	Long idPeerToPeer=null;
	Long idOperation=null;
	
//On cherche d'abord le pret	
List<Pret>listerPret=listerPret(rb.getNoCompte_me().getNoCompte_me(), "Pret", "Non acquite");	
for(Pret p:listerPret){
idPeerToPeer=p.getPeerToPeer().getId();
idOperation=p.getId_op();
	
}
//On enregistre le remboursement
em.persist(rb);
//On met a jour la somme versee et la somme due du pretteur
PeerToPeer peer1=em.find(PeerToPeer.class, idPeerToPeer);
peer1.setSommeVersee(peer1.getSommeVersee()+rb.getMontant_op());
peer1.setSommeDue(peer1.getSommeDue()-rb.getMontant_op());
em.merge(peer1);

//Ajout du montant au solde de l'emprunteur
CompteMembre cp=em.find(CompteMembre.class, peer1.getEmpruteur().getNoCompte_me());
cp.setSolde_compte(cp.getSolde_compte()+rb.getMontant_op());
em.merge(cp);

//Rechercher sur la table operation pour mettre a jour le statut du pret
PeerToPeer peer2=em.find(PeerToPeer.class, idPeerToPeer);
Pret p=em.find(Pret.class, idOperation);
if(peer2.getSommeDue()==0){
	p.setStatut_pret("Acquite");
	em.merge(p);
}


}

@Override
public List<Remboursement> listerRemboursement(String numeroCompte, String critere) {
	Query req=em.createQuery("SELECT p FROM Remboursement p WHERE NoCompte=:x AND TypeOperation=:y");
	req.setParameter("x", numeroCompte);
	req.setParameter("y", critere);
	List<Remboursement> list=req.getResultList();
	return list;
}

@Override
public void retrait(Retrait rt) {
	em.persist(rt);
	CompteMembre c=em.find(CompteMembre.class, rt.getNoCompte_me().getNoCompte_me());
c.setSolde_compte(c.getSolde_compte()-rt.getMontant_op());
em.merge(c);
}

@Override
public List<Retrait> listerRetrait(String numeroCompte, String critere) {
	Query req=em.createQuery("SELECT p FROM Retrait p WHERE NoCompte=:x AND TypeOperation=:y");
	req.setParameter("x", numeroCompte);
	req.setParameter("y", critere);
	List<Retrait> list=req.getResultList();
	return list;
}

@Override
public void saveDepotMoncash(DepotMoncash d) {
	em.persist(d);
	
}

@Override
public DepotMoncash getDepotMoncash(Long id) {
	DepotMoncash d=em.find(DepotMoncash.class, id);
	return d;
}

@Override
public void deletDepotMonCash(Long id) {
	// TODO Auto-generated method stub
	
}

@Override
public void tranfere(String noCompte1, String noCompte2, double montant) {
	CompteMembre c1=em.find(CompteMembre.class, noCompte1);
	CompteMembre c2=em.find(CompteMembre.class, noCompte2);
	
	
	Transfert t=new Transfert(montant, new Date(),new CompteMembre(noCompte1), noCompte2);
	em.persist(t);
		
	c1.setSolde_compte(c1.getSolde_compte()-montant);
	em.merge(c1);
	
	c2.setSolde_compte(c2.getSolde_compte()+montant);
	em.merge(c2);
}

@Override
public List<Transfert> listerTransfert(String noCompte,String critere) {
	Query req=em.createQuery("SELECT op FROM Operation op WHERE NoCompte=:x AND TypeOperation=:y");
	req.setParameter("x", noCompte);
	req.setParameter("y", critere);
	List<Transfert> list=req.getResultList();
			return list;
}

@Override
public ProfileMembre getNoCompte(String id) {
	Query req=em.createNativeQuery("SELECT NoCompte from Profilemembre where phone_me=:x");
	ProfileMembre p=new ProfileMembre();
	
	return p;
}

@Override
public List<ProfileMembre> lister(String critere) {
	Query req=em.createQuery("SELECT op FROM ProfileMembre op WHERE phone_me=:x OR cin_nif_me=:y");
	req.setParameter("x", critere);
	req.setParameter("y", critere);
	List<ProfileMembre> list=req.getResultList();
			return list;
}

@Override
public DesactiverLien getLien(String id) {
	 DesactiverLien d=em.find( DesactiverLien.class, id);
	return d;
}

@Override
public void modifier(ProfileMembre auth) {
em.merge(auth);
	
}

@Override
public List<Pret> listerPret(String numeroCompte, String critere,String statut_pret) {
	Query req=em.createQuery("SELECT op FROM Operation op WHERE NoCompte=:x AND TypeOperation=:y AND statut_pret=:z");
	req.setParameter("x", numeroCompte);
	req.setParameter("y", critere);
	req.setParameter("z", statut_pret);
	List<Pret> list=req.getResultList();
			return list;
}

@Override
public List<Placement> listerPlacement(Boolean etat, String critere,double montant) {
	Query req=
			em.createQuery("SELECT op FROM Operation op WHERE  etat_Pla=:x AND TypeOperation=:y AND montant=:z");
	req.setParameter("x", etat);
	req.setParameter("y", critere);
	req.setParameter("z", montant);
	List<Placement> list=req.getResultList();
			return list;
}


private List<Placement> listerPlacement1(Boolean etat, String critere,double montant) {
	Query req=em.createQuery("SELECT op FROM Operation op WHERE  etat_Pla=:x AND TypeOperation=:y AND montant=:z ORDER BY id_op ASC ");
	req.setParameter("x", etat);
	req.setParameter("y", critere);
	req.setParameter("z", montant);
	req.setMaxResults(1).getResultList();
	List<Placement> list=req.getResultList();
			return list;
}

//Query req=em.createQuery("SELECT e FROM operation e WHERE  etat_Pla=:x AND TypeOperation=:y AND montant=:z ORDER BY IdOperation ASC ");


@Override
public List<Placement> listerPlacement(Boolean etat, String critere) {
        Query req=em.createQuery("SELECT op FROM Operation op WHERE etat_Pla=:x AND TypeOperation=:y ORDER BY IdOperation ASC");
		req.setParameter("x", etat);
		req.setParameter("y", critere);
		List<Placement> list=req.getResultList();
				return list;
	}



}
