package com.banco.pagination;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wraps all the information needed to paginate a table.
 * Basado en implementación de Roberto Cortez
 * @author jmartinez
 * @param <T> Tipo generico para la lista de entidades a paginar
 */
@XmlRootElement
public class PaginatedListWrapper<T> implements Serializable {
    
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalResults;

    private String sortFields;
    private String sortDirections;
    
    private static final Logger LOGGER = Logger.getLogger(PaginatedListWrapper.class.getName());
            
    @XmlElement
    private List<T> list;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public String getSortFields() {
        return sortFields;
    }

    public void setSortFields(String sortFields) {
        this.sortFields = sortFields;
    }

    public String getSortDirections() {
        return sortDirections;
    }

    public void setSortDirections(String sortDirections) {
        this.sortDirections = sortDirections;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
      LOGGER.log(Level.FINEST,"Se inicializa lista en PaginatedListWrapper.setList()");
      this.list = list;
    }
}
