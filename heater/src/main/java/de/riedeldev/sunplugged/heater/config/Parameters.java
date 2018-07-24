package de.riedeldev.sunplugged.heater.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import lombok.Getter;
import lombok.Setter;

@Component
@ApplicationScope
public class Parameters {
	@Getter
	@Setter
	private double preHeaterP = 1.0;
	@Getter
	@Setter
	private double preHeaterI = 20.0;
	@Getter
	@Setter
	private double preHeaterD = 5.0;
	@Getter
	@Setter
	private double preHeaterIntervalLength = 2.0;

	@Getter
	@Setter
	private double mainHeaterP = 1.0;

	@Getter
	@Setter
	private double mainHeaterI = 1.0;

	@Getter
	@Setter
	private double mainHeaterD = 5.0;

	@Getter
	@Setter
	private double mainHeaterIntervalLength = 5.0;

	private List<Consumer<Parameters>> listener = new LinkedList<>();

	@PostConstruct
	protected void postConstruct() {
		reloadParameters();
	}

	public void reloadParameters() {
		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		Yaml yaml = new Yaml(representer);
		try (InputStream is = new FileInputStream("config/para.yml")) {

			Parameters para = yaml.loadAs(is, Parameters.class);
			if (para != null && this != null) {
				BeanUtils.copyProperties(para, this);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void saveParameters() {

		listener.forEach(consumer -> consumer.accept(this));

		DumperOptions options = new DumperOptions();
		options.setIndent(4);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		Representer representer = new Representer();
		representer
				.setPropertyUtils(new SortingByFieldsPropertyUtils(getClass()));
		Yaml yaml = new Yaml(representer, options);

		try (Writer writer = new FileWriter("config/para.yml")) {
			writer.write(yaml.dumpAs(this, Tag.MAP, FlowStyle.BLOCK));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void registerListener(Consumer<Parameters> consumer) {
		this.listener.add(consumer);
	}

}
