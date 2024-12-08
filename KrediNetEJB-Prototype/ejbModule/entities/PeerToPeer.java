package entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class PeerToPeer implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	public PeerToPeer(Long id) {
		super();
		this.id = id;
	}

	@Column(nullable=false)
	private double sommeVersee;
	@Column(nullable=false)
	private double sommeDue;
	public PeerToPeer(double sommeVersee, double sommeDue,  CompteMembre empruteur,
			CompteMembre investisseur, double montant) {
		super();
		this.sommeVersee = sommeVersee;
		this.sommeDue = sommeDue;
		this.operation = operation;
		Empruteur = empruteur;
		this.investisseur = investisseur;
		this.montant = montant;
	}

	public PeerToPeer(double sommeVersee, double sommeDue, CompteMembre empruteur, CompteMembre investisseur,
			Collection<Pret> pret, double montant) {
		super();
		this.sommeVersee = sommeVersee;
		this.sommeDue = sommeDue;
		Empruteur = empruteur;
		this.investisseur = investisseur;
		this.pret = pret;
		this.montant = montant;
	}

	public double getSommeVersee() {
		return sommeVersee;
	}

	public void setSommeVersee(double sommeVersee) {
		this.sommeVersee = sommeVersee;
	}

	public double getSommeDue() {
		return sommeDue;
	}

	public void setSommeDue(double sommeDue) {
		this.sommeDue = sommeDue;
	}

	@ManyToOne
	@JoinColumn
	private Operation operation;
	
	@ManyToOne
	@JoinColumn
	private CompteMembre Empruteur;
	
	@ManyToOne
	@JoinColumn
	private CompteMembre investisseur;
	
	@OneToMany(mappedBy="peerToPeer")
	private Collection<Pret> pret;
	public Collection<Pret> getPret() {
		return pret;
	}

	public void setPret(Collection<Pret> pret) {
		this.pret = pret;
	}

	public PeerToPeer(Operation operation, CompteMembre empruteur, CompteMembre investisseur, double montant) {
		super();
		this.operation = operation;
		Empruteur = empruteur;
		this.investisseur = investisseur;
		this.montant = montant;
	}

	public PeerToPeer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public CompteMembre getEmpruteur() {
		return Empruteur;
	}

	public void setEmpruteur(CompteMembre empruteur) {
		Empruteur = empruteur;
	}

	public CompteMembre getInvestisseur() {
		return investisseur;
	}

	public void setInvestisseur(CompteMembre investisseur) {
		this.investisseur = investisseur;
	}

	public double getMontant() {
		return montant;
	}

	public void setMontant(double montant) {
		this.montant = montant;
	}

	private double montant;
}
