package es.pryades.imedig.cloud.modules.Administration.tabs;

import java.util.Date;

import lombok.Getter;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.core.common.TableImedig;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
public abstract class AdminContent extends VerticalLayout
{
	@Getter protected ImedigContext context;

	protected Label lbDateSelected;

	protected PopupDateField popUpDateSelected;

	protected Button bttnAplicaFiltro;

	protected HorizontalLayout cellFechaSelected;
	protected HorizontalLayout cellDispositivo;
	protected HorizontalLayout cellApplyFilterBttn;

	protected HorizontalLayout adminCntOpBusquedaContainer;
	protected VerticalLayout adminCntResultLayout;
	protected HorizontalLayout oppLayout;

	protected String tabTitle;
	protected String adminCntOpBusquedaFormTitle;
	protected String adminCntResultLayoutTitle;

	protected abstract void addPopUpDateSelectedValueChangerListener();

	protected abstract void addComboBoxDispoValueChangerListener();

	public abstract void addButtonApplyFilterClickListener();

	public abstract TableImedig getTableValues();

	public AdminContent(ImedigContext ctx)
	{
		this.context = ctx;

		this.adminCntOpBusquedaContainer = new HorizontalLayout();
		this.adminCntResultLayout = new VerticalLayout();

	}

	public AdminContent()
	{
		this.context = null;

		this.adminCntOpBusquedaContainer = new HorizontalLayout();
		this.adminCntResultLayout = new VerticalLayout();
	}

	// ###########################################################################################//
	// # ACCESS METHOD(S) #//
	// ###########################################################################################//

	/**
	 * 
	 * @return String -> The current TabTitle
	 */
	public String getTabTitle()
	{
		return this.tabTitle;
	}

	/**
	 * 
	 * @param String
	 *            -> The new Tab Title
	 */
	public void setTabTitle(String tabTitle)
	{
		this.tabTitle = tabTitle;
	}

	// ###########################################################################################//
	// # GENERAL PROPOUSE VALIDATION METHODS #//
	// ###########################################################################################//

	protected String validateBeforeApplyFilters()
	{
		if (popUpDateSelected.getValue() != null)
		{
			return "";
		}
		else
		{
			// la fecha no puede ser vacia
			return getContext().getString( "adminContent.ErrorNullMessage");
		}
	}

	// ###########################################################################################//
	// # OTHER METHODS #//
	// ###########################################################################################//

	protected void initComponents() throws ImedigException
	{
		this.lbDateSelected = new Label(getContext().getString( "adminContent.lbAdminFilterDateSelected"));
		this.lbDateSelected.setWidth("55px");

		this.popUpDateSelected = new PopupDateField();
		this.popUpDateSelected.setWidth("120px");
		this.popUpDateSelected.setResolution(Resolution.DAY); //OJO DISMER
		this.popUpDateSelected.setImmediate(true);
		this.popUpDateSelected.setDateFormat(getContext().getString( "adminContent.dateFormat"));
		this.popUpDateSelected.setValue(new Date());
		this.popUpDateSelected.setRequired(true);
		this.addPopUpDateSelectedValueChangerListener();

		bttnAplicaFiltro = new Button();
		bttnAplicaFiltro.setStyleName("borderless icon-on-top");
		bttnAplicaFiltro.setDescription(getContext().getString( "adminContent.bttnAplicaFiltro"));
		bttnAplicaFiltro.setIcon(new ThemeResource("images/accept.png"));

		cellFechaSelected = new HorizontalLayout();
		cellFechaSelected.setWidth("240px");
		cellFechaSelected.addComponent(lbDateSelected);
		cellFechaSelected.setComponentAlignment(lbDateSelected, Alignment.BOTTOM_RIGHT);
		cellFechaSelected.addComponent(popUpDateSelected);
		cellFechaSelected.setComponentAlignment(popUpDateSelected, Alignment.BOTTOM_LEFT);
		cellFechaSelected.setExpandRatio(popUpDateSelected, 1.0f);
		cellFechaSelected.setMargin(new MarginInfo( false, true, false, false) );

		cellApplyFilterBttn = new HorizontalLayout();
		cellApplyFilterBttn.setWidth("38px");
		cellApplyFilterBttn.addComponent(bttnAplicaFiltro);
		cellApplyFilterBttn.setComponentAlignment(bttnAplicaFiltro, Alignment.MIDDLE_RIGHT);
		cellApplyFilterBttn.setMargin(new MarginInfo(false, true, false, false));

		this.adminCntOpBusquedaContainer.setHeight("40px");
		this.adminCntOpBusquedaContainer.setWidth("-1px");
		this.adminCntOpBusquedaContainer.addComponent(cellFechaSelected);
		this.adminCntOpBusquedaContainer.addComponent(cellDispositivo);
		this.adminCntOpBusquedaContainer.addComponent(cellApplyFilterBttn);
		this.adminCntOpBusquedaContainer.setMargin(new MarginInfo(true, false, false, true));

		this.adminCntResultLayout.setWidth("100%");
		this.adminCntResultLayout.setHeight("100%");

		this.addComponent(this.adminCntOpBusquedaContainer);
		this.addComponent(this.adminCntResultLayout);
		this.setExpandRatio(this.adminCntResultLayout, 1.0f);

	}
}
