ALTER TABLE "public"."hs_dict_device_graph_item" ADD COLUMN "suffix" varchar(255);
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."suffix" IS '后缀';