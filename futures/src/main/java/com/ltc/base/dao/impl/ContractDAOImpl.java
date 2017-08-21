package com.ltc.base.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.ltc.base.dao.ContractDAO;
import com.ltc.base.helpers.BaseConstant;
import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public class ContractDAOImpl extends HibernateDaoSupport implements ContractDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractVO> getActiveContractList() {
		String hql = "From ContractVO where status=:status";
		List<ContractVO> contractList = this.currentSession().createQuery(hql)
				.setParameter("status", BaseConstant.ACTIVE).list();
		logger.debug("[ContractDAOImpl] get fresh active contract list: "
				+Arrays.toString(contractList.toArray(new ContractVO[0])));
		return contractList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractMetaVO> getContractMetaList() {
		return this.currentSession().createCriteria(ContractMetaVO.class).list();
	}

	@Override
	public ContractVO getNextMainContract(ContractVO c) {
		String hql = "From ContractVO where contractMeta=:contractMeta and status=:status";
		ContractVO nmc = (ContractVO) this.currentSession().createQuery(hql)
				.setParameter("contractMeta", c.getContractMeta())
				.setParameter("status", BaseConstant.NEXT_MAIN)
				.uniqueResult();
		return nmc;
	}

	@Override
	public void saveContract(ContractVO nmc) {
		this.currentSession().saveOrUpdate(nmc);
	}

	@Override
	public void mainSwitch(ContractVO currentC, ContractVO newC) {
		ContractVO dbContract = (ContractVO) this.currentSession().load(ContractVO.class, currentC);
		ContractVO dbNewContract = (ContractVO) this.currentSession().load(ContractVO.class, newC);
		String hql = "update ContractVO set status=:status where symbol=:symbol and prid=:prid";
		if(dbContract != null){
			dbContract.setStatus(currentC.getStatus());
			this.currentSession().saveOrUpdate(dbContract);
		} else {
//			this.currentSession().saveOrUpdate(currentC);
			this.currentSession().createQuery(hql)
				.setParameter("status", currentC.getStatus())
				.setParameter("symbol", currentC.getContractMeta().getSymbol())
				.setParameter("prid", currentC.getPrid()).executeUpdate();
		}
		if(dbNewContract != null){
			dbNewContract.setStatus(newC.getStatus());
			this.currentSession().saveOrUpdate(dbNewContract);
		} else {
			this.currentSession().createQuery(hql)
				.setParameter("status", newC.getStatus())
				.setParameter("symbol", newC.getContractMeta().getSymbol())
				.setParameter("prid", newC.getPrid()).executeUpdate();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractVO> getNextMainContractList() {
		String hql = "From ContractVO where status=:status";
		List<ContractVO> nmc = (List<ContractVO>) this.currentSession().createQuery(hql)
				.setParameter("status", BaseConstant.NEXT_MAIN).list();
		return nmc;
	}

	@Override
	public void saveContractMeta(ContractMetaVO contractMeta) {
		String hql = "update ContractMetaVO set atr=:atr, atrUpdateDate=:atrUpdateDate where symbol=:symbol";
		this.currentSession().createQuery(hql)
			.setParameter("atr", contractMeta.getAtr())
			.setParameter("atrUpdateDate", contractMeta.getAtrUpdateDate())
			.setParameter("symbol", contractMeta.getSymbol()).executeUpdate();
	}

}
