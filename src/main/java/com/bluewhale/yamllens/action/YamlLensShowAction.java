package com.bluewhale.yamllens.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE_ARRAY;

public class YamlLensShowAction extends AnAction {
	private static final String DEFAULT_PROFILE = "default";
	private static final String PROPERTY_OF_ACTIVATE_PROFILE = "spring.config.activate.on-profile";
	private static final Pattern PROFILE_PATTERN = Pattern.compile("application-(.*).yml");

	@Override
	public void actionPerformed(AnActionEvent e) {
		// 현재 선택된 파일 가져오기
		var psiFiles = e.getData(VIRTUAL_FILE_ARRAY);
		if (psiFiles == null) {
			return;
		}

		// 테이블 생성 및 표시
		var model = buildTableModel(psiFiles);

		// 테이블 생성 및 표시
		showUi(model);
	}

	@NotNull
	private DefaultTableModel buildTableModel(VirtualFile[] psiFiles) {
		var model = new DefaultTableModel();
		model.addColumn("Property");
		model.addColumn("Profile");
		model.addColumn("Value");

		var profilePropertyMap = getProfilePropertyMap(psiFiles);
		var profiles = getProfiles(profilePropertyMap.keySet());
		var propertyProfileMap = getPropertyProfileMap(profilePropertyMap);

		propertyProfileMap.forEach((propertyName, env) ->
									   profiles.forEach(profile ->
															appendProperties(model, propertyName, env, profile)
													   ));
		return model;
	}

	private void showUi(DefaultTableModel model) {
		var table = new JBTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(new Dimension(900, 800));

		var columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(300);
		columnModel.getColumn(1).setPreferredWidth(200);
		columnModel.getColumn(2).setPreferredWidth(400);

		var scrollPane = new JBScrollPane(table);
		var frame = new JFrame("YAML Lens");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane()
			 .add(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}

	@NotNull
	private Map<String, Map<String, String>> getProfilePropertyMap(VirtualFile[] psiFiles) {
		return Arrays.stream(psiFiles)
					 .map(file -> new FileSystemResource(file.getPath()))
					 .map(this::getLoad)
					 .flatMap(Collection::stream)
					 .collect(Collectors.toMap(this::getProfileName, this::getProperties));
	}

	@NotNull
	private TreeMap<String, Map<String, String>> getPropertyProfileMap(Map<String, Map<String, String>> profilePropertyMap) {
		return profilePropertyMap.values().stream()
								 .map(Map::keySet)
								 .flatMap(Collection::stream)
								 .distinct()
								 .collect(Collectors.toMap(Function.identity(),
														   name -> profilePropertyMap.entrySet()
																					 .stream()
																					 .collect(Collectors.toMap(Map.Entry::getKey,
																											   o -> o.getValue().getOrDefault(name, StringUtils.EMPTY))),
														   this::uniqKeysMapMerger,
														   TreeMap::new));
	}

	private void appendProperties(DefaultTableModel model, String propertyName, Map<String, String> env, String profile) {
		model.addRow(new Object[] {propertyName, profile, env.getOrDefault(profile, StringUtils.EMPTY)});
	}

	private List<PropertySource<?>> getLoad(Resource resource) {
		try {
			return new YamlPropertySourceLoader().load(resource.getFilename(), resource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getProfileName(PropertySource<?> propertySource) {
		return Optional.ofNullable(propertySource.getProperty(PROPERTY_OF_ACTIVATE_PROFILE))
					   .map(Object::toString)
					   .orElseGet(() -> getProfileNameByFileName(propertySource.getName(), DEFAULT_PROFILE));
	}

	private String getProfileNameByFileName(String fileName, String defaultProfile) {
		var matcher = PROFILE_PATTERN.matcher(fileName);
		if (matcher.matches()) {
			return matcher.group(1);
		}

		return defaultProfile;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getProperties(PropertySource<?> propertySource) {
		var source = (Map<Object, Object>)propertySource.getSource();
		return source.entrySet().stream()
					 .collect(Collectors.toMap(o -> o.getKey().toString(), o -> o.getValue().toString()));
	}

	private List<String> getProfiles(Set<String> profileSet) {
		return profileSet.stream()
						 .sorted((o1, o2) -> (o1.equals(DEFAULT_PROFILE) ? -1 : (o2.equals(DEFAULT_PROFILE)) ? 1 : o1.compareTo(o2)))
						 .collect(Collectors.toList());
	}

	private <K, V> Map<K, V> uniqKeysMapMerger(Map<K, V> m1, Map<K, V> m2) {
		for (var e : m2.entrySet()) {
			var k = e.getKey();
			var v = Objects.requireNonNull(e.getValue());
			m1.putIfAbsent(k, v);
		}
		return m1;
	}
}
