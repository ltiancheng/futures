package com.ltc.base.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ltc.base.dao.ContractDAO;
import com.ltc.base.helpers.BaseConstant;
import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public class ContractDAOImpl extends HibernateDaoSupport implements ContractDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractVO> getActiveContractList() {
		String hql = "From ContractVO where status=:status";
		List<ContractVO> contractList = this.getSession().createQuery(hql)
				.setParameter("status", BaseConstant.ACTIVE).list();
		logger.debug("[ContractDAOImpl] get fresh active contract list: "
				+Arrays.toString(contractList.toArray(new ContractVO[0])));
		return contractList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractMetaVO> getContractMetaList() {
		return this.getSession().createCriteria(ContractMetaVO.class).list();
	}

	@Override
	public ContractVO getNextMainContract(ContractVO c) {
		String hql = "From ContractVO where contractMeta=:contractMeta and status=:status";
		ContractVO nmc = (ContractVO) this.getSession().createQuery(hql)
				.setParameter("contractMeta", c.getContractMeta())
				.setParameter("status", BaseConstant.NEXT_MAIN)
				.uniqueResult();
		return nmc;
	}

	@Override
	public void saveContract(ContractVO nmc) {
		this.getSession().saveOrUpdate(nmc);
	}

	@Override
	public void mainSwitch(ContractVO currentC, ContractVO newC) {
		ContractVO dbContract = (ContractVO) this.getSession().load(ContractVO.class, currentC);
		ContractVO dbNewContract = (ContractVO) this.getSession().load(ContractVO.class, newC);
		if(dbContract != null){
			dbContract.setStatus(currentC.getStatus());
			this.getSession().saveOrUpdate(dbContract);
		} else {
			this.getSession().saveOrUpdate(currentC);
		}
		if(dbNewContract != null){
			dbNewContract.setStatus(newC.getStatus());
			this.getSession().saveOrUpdate(dbNewContract);
		} else {
			this.getSession().saveOrUpdate(newC);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractVO> getNextMainContractList() {
		String hql = "From ContractVO where status=:status";
		List<ContractVO> nmc = (List<ContractVO>) this.getSession().createQuery(hql)
				.setParameter("status", BaseConstant.NEXT_MAIN).list();
		return nmc;
	}

}
