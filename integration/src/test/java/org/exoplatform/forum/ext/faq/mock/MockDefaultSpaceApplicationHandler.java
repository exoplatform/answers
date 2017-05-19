package org.exoplatform.forum.ext.faq.mock;

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.social.core.space.SpaceApplicationConfigPlugin;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.impl.DefaultSpaceApplicationHandler;
import org.exoplatform.social.core.space.model.Space;

/**
 * Mocked DefaultSpaceApplicationHandler to avoid creating applications when creating spaces
 * since it is not necessary for the tests.
 */
public class MockDefaultSpaceApplicationHandler extends DefaultSpaceApplicationHandler {
  /**
   * Constructor.
   *
   * @param dataStorage
   * @param pageService
   */
  public MockDefaultSpaceApplicationHandler(DataStorage dataStorage, PageService pageService) {
    super(dataStorage, pageService);
  }

  @Override
  public void initApps(Space space, SpaceApplicationConfigPlugin spaceApplicationConfigPlugin) throws SpaceException {
    // do nothing
  }

  @Override
  public void deInitApp(Space space) throws SpaceException {
    // do nothing
  }

  @Override
  public void activateApplication(Space space, String appId, String appName) throws SpaceException {
    // do nothing
  }

  @Override
  public void deactiveApplication(Space space, String appId) throws SpaceException {
    // do nothing
  }

  @Override
  public void installApplication(Space space, String appId) throws SpaceException {
    // do nothing
  }

  @Override
  public void removeApplication(Space space, String appId, String appName) throws SpaceException {
    // do nothing
  }

  @Override
  public void removeApplications(Space space) throws SpaceException {
    // do nothing
  }
}
