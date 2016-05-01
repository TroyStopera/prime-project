(function(){
	function CartTracker()
	{
		var _updateHeader = function(cart) {
			$("header ul li.cart a").text("Cart: " +json.count+ " items, (" +json.total+ ")");
		};

		this.update = function() {
			$.ajax({
				"method": "GET",
				"url": "/api/listCartItems",
				"dataType": "json"
			}).done( function(json) {
				_updateHeader(json);
			} );
		};

		this.updateCartQuantity = function(item, quantity) {
			var param = {
				"itemId": item,
				"quantity": quantity
			};

			$.ajax({
				"method": "GET",
				"url": "/api/updateCartQuantity",
				"data": param,
				"dataType": "json"
			}).done( function(json) {
				_updateHeader(json);
			} );
		};

		this.addToCart = function(item, quantity) {
			var param = {
				"itemId": item,
				"quantity": quantity
			};

			$.ajax({
				"method": "GET",
				"url": "/api/addCartItem",
				"data": param,
				"dataType": "json"
			}).done( function(json) {
				_updateHeader(json);
			} );
		};
	}

	Primes = {};
    Primes.CartTracker = new CartTracker();
})();