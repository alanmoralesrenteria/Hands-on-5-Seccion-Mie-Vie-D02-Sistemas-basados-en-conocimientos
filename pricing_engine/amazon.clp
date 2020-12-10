(import pricing_engine.*)
(deftemplate Order       (declare (from-class Order)))
(deftemplate OrderItem   (declare (from-class OrderItem)))
(deftemplate CatalogItem (declare (from-class CatalogItem)))
(deftemplate Customer    (declare (from-class Customer)))
(deftemplate CreditCard  (declare (from-class CreditCard)))


(defrule 20%-amazon-discount "Obten un 20% de descuento en la compra de mas de $10000 en Amazon" ?o <- (Order {total > 10000})
    => (add (new Offer "Obten un 20% de descuento en la compra de mas de $10000 en Amazon" (/ ?o.total 20))))

(defrule 12-meses-banamex "Obten 12 meses sin intereses al pargar con tarjetas Banamex" ?o <- (Order (tarjeta "Banamex"))
    => (add (new Offer "Obten 12 meses sin intereses al pagar con tarjetas Banamex" ?o.tarjeta)))

(defrule 5%-amazon-item-discount "Obten un 5% de descuento en la compra de mas de 5 articulos identicos (mayoreo)." ?o<-(OrderItem {quantity >= 5} (price ?price) (description ?itemdescription))
    => (add (new Offer "Obten un 5% de descuento en la compra de mas de 5 articulos identicos(mayoreo)." ?itemdescription (* (* ?price 0.05) ?o.quantity) )))

(defrule Nintendo_Consola_Switch-promo "Compra 5 Nintendo Consola Switch y paga 4" (OrderItem {partNumber == 111} {quantity == 5} (price ?price))
    => (add (new Offer "Compra 5 Nintendo Consola Switch y paga 4" ?price )))

(defrule demons-promo "Compra una Playstation 5 y junto con el Demons Souls y solo paga el playstation." (OrderItem {partNumber == 112}) (OrderItem {partNumber == 115} (price ?price))
		=> (add (new Offer "Compra una Playstation 5 y junto con el Demons Souls y solo paga el playstation." ?price)))

(defrule samsung-promo "Compra una samsung note 10 con tarjeta Banamex, y obten un 10% de descuento"  ?o <- (Order (tarjeta "Banamex")) (OrderItem {partNumber == 114}(price ?price))
    => (add (new Offer "Compra una samsung note 10 con tarjeta Banamex, y obten un 10% de descuento" (/ ?price 10) )))