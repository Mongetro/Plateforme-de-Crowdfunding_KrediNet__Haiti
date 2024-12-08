package entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
@Entity
@DiscriminatorValue(value="Remboursement")
public class Remboursement extends Operation implements Serializable{

	private static final long serialVersionUID = 1L;

	public Remboursement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Remboursement(double montant_op, Date date_Op, CompteMembre noCompte_me) {
		super(montant_op, date_Op, noCompte_me);
		// TODO Auto-generated constructor stub
	}

	public Remboursement(Long id_op) {
		super(id_op);
		// TODO Auto-generated constructor stub
	}



}
