package com.seacreeper.craftsperson.views.creeper.postmannewman;

import com.seacreeper.craftsperson.service.scribe.postmannewman.ScribePostmanNewmanTalker;
import com.seacreeper.craftsperson.service.scribe.postmannewman.model.Data;
import com.seacreeper.craftsperson.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "creeper/postmannewman", layout = MainLayout.class)
@PageTitle("Creeper: POSTMAN-NEWMAN")
public class PostmanNewmanDefaultView extends SplitLayout {

  @Autowired
  private ScribePostmanNewmanTalker scribePostmanNewmanTalker;
  private Grid grid;

  public PostmanNewmanDefaultView() {
    this.grid = new Grid<>(Data.class);
    configureHistoryGrid();
    addToPrimary(getPrimary());
    addToSecondary(getSecondary());
    setOrientation(Orientation.VERTICAL);
    setSplitterPosition(40);
    setHeightFull();
  }

  private void configureHistoryGrid() {
    grid.setColumns("timestamp", "url");
    grid.recalculateColumnWidths();
    grid.setHeightFull();
    setOrientation(Orientation.VERTICAL);
    setSplitterPosition(40);
    setHeightFull();
  }

  private Component getPrimary() {
    val primary = new SplitLayout();
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(buffer);
    upload.addSucceededListener(
        event -> {
          String fileName = event.getFileName();
          InputStream inputStream = buffer.getInputStream(fileName);
          // Do something with the file data
          // processFile(inputStream, fileName);
        });
    val primaryVertical = new VerticalLayout();
    primaryVertical.setSizeFull();
    primaryVertical.add(new H2("Add Postman Collection"));
    primaryVertical.add(upload);
    primaryVertical.setAlignItems(Alignment.CENTER);
    primary.addToPrimary(primaryVertical);
    Tab arguments = new Tab("Arguments");
    Tab environment = new Tab("Environment");
    Tab configure = new Tab("Configure");
    Tabs tabs = new Tabs(arguments, environment, configure);
    tabs.setWidthFull();
    val secondaryVertical = new VerticalLayout();
    secondaryVertical.add(tabs);
    val testRun = new Button("Dry run");
    val submit = new Button("Submit");
    submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    val buttonsHorizontal = new HorizontalLayout();
    buttonsHorizontal.add(testRun, submit);
    buttonsHorizontal.setPadding(true);
    secondaryVertical.add(buttonsHorizontal);
    primary.addToSecondary(secondaryVertical);
    return primary;
  }

  private Component getSecondary() {
    val interactiveSection = new SplitLayout(grid, getDetails());
    interactiveSection.setSplitterPosition(75);
    return interactiveSection;
  }

  private Component getDetails() {
    val details =
        new Details(
            "About POSTMAN-NEWMAN Creeper",
            new Text("HTTP Creeper runs Postman collection on Newman command-line runner"));
    return details;
  }

  @PostConstruct
  private void recent() {
    try {
      val future = scribePostmanNewmanTalker.readRecent(Optional.empty());
      val result = future.get();
      grid.setItems(result.getData());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
