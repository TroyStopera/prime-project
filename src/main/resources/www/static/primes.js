(function(){
	function CartTracker()
	{
		var _updateHeader = function(cart) {
			$("header ul li.cart a").text("Cart: " +json.count+ " items, (" +json.total+ ")");
		};

		this.update = function() {
			$.ajax({
				"url": "/api/listCartItems",
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
				"url": "/api/addCartItem",
				"data": JSON.stringify(param),
				"dataType": "json"
			}).done( function(json) {
				_updateHeader(json);
			} );
		};

		this.removeFromCart = function(item, quantity) {
			var param = {
				"itemId": item,
				"quantity": quantity
			};

			$.ajax({
				"url": "/api/removeCartItem",
				"data": JSON.stringify(param),
				"dataType": "json"
			}).done( function(json) {
				_updateHeader(json);
			} );
		};
	}

	Primes = {};
    Primes.CartTracker = new CartTracker();
})();