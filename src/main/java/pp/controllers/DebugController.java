package pp.controllers;

import pp.HTMLController;

public class DebugController
{
	public static class DashboardController extends HTMLController
	{
		@Override
		protected void generatePage() throws Exception
		{
			outputView("/www/views/header.hbs");
			outputView("/www/debug/dashboard.hbs");
			outputView("/www/views/footer.hbs");
		}
	}

	public static class ItemDBViewer extends HTMLController
	{
		@Override
		protected void generatePage() throws Exception
		{
			outputView("/www/views/header.hbs");
			outputView("/www/debug/item_db.hbs")
				.bindData("item", dao().itemAccessor().allItems());
			outputView("/www/views/footer.hbs");
		}
	}
}
